package com.thought.monkey.handler.ws;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import lombok.extern.java.Log;
import com.thought.monkey.codec.ws.FrameCodec;
import com.thought.monkey.handler.ConnectHandler;
import com.thought.monkey.server.distributed.NettyWorker;
import com.thought.monkey.wsmessage.MessageInfo;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 *
 *
 * retain 保持某个对象的引用，
 */
@Log
@ChannelHandler.Sharable
public class HttpHandler extends SimpleChannelInboundHandler<Object> {


    //客户端和服务端websocket 的握手
    private WebSocketServerHandshaker handshaker;

    final ConnectHandler connectHandler = new ConnectHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println(msg);
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;

            if (upgradeToWebSocket(ctx,request))  {
                ctx.fireChannelRead(((FullHttpRequest) msg).retain());
            } else {
                handleHttpRequest(ctx, request);
            }
        } else if (msg instanceof WebSocketFrame) {
            ctx.fireChannelRead(((WebSocketFrame) msg).retain());
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
        log.info(response.toString());

    }

    private boolean upgradeToWebSocket(ChannelHandlerContext ctx, FullHttpRequest req) {

        HttpHeaders headers = req.headers();
        if ("Upgrade".equalsIgnoreCase(headers.get(HttpHeaderNames.CONNECTION)) &&
                "WebSocket".equalsIgnoreCase(headers.get(HttpHeaderNames.UPGRADE))) {
            ChannelPipeline pipeline = ctx.pipeline();
            //将http升级为WebSocket
//            pipeline.addLast(new WebSocketServerProtocolHandler("/websocket"));
            log.info("port ==> "+String.valueOf( NettyWorker.getInstance().getWsNode().getPort()));
            WebSocketServerHandshakerFactory wsFactory =
                    new WebSocketServerHandshakerFactory("ws://localhost:"+ NettyWorker.getInstance().getWsNode().getPort() +"/websocket",
                            null,false);
            handshaker =  wsFactory.newHandshaker(req);

            if (null == handshaker) {
                /**
                 * HTTP 426 Upgrade Required客户端错误响应代码指示服务器拒绝使用当前协议执行请求，但可能在客户端升级到其他协议后愿意这样做。
                 *
                 * 客户端不支持此协议
                 * */
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                handshaker.handshake(ctx.channel(),req);
            }

            //增加编解码


            pipeline.addLast("frame-codec",new FrameCodec(handshaker));
            pipeline.addLast("protobuf-decode",new ProtobufDecoder(MessageInfo.Model.getDefaultInstance()));
            //自定义handler，处理消息

//
            pipeline.addLast("connectHandler",connectHandler);

            pipeline.remove(this);
            //向下传递
            pipeline.fireChannelActive();
            return true;

        }

        return false;
    }
}
