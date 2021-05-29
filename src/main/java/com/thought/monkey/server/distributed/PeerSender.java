package com.thought.monkey.server.distributed;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.java.Log;

import com.thought.monkey.codec.protobuf.ProtoCodec;
import com.thought.monkey.entity.Notification;
import com.thought.monkey.entity.WsNode;
import com.thought.monkey.handler.WsNodeHeartBeatHandler;
import com.thought.monkey.protoBuilder.NotificationMsgBuilder;
import com.thought.monkey.util.JsonUtil;
import com.thought.monkey.wsmessage.MessageInfo;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 */
@Data
@Log
public class PeerSender {

    private Channel channel;

    private WsNode wsNode;

    private  int reConnectCount=0;

    //唯一标记
    private boolean connectFlag;


    private Bootstrap b;

    private EventLoopGroup g;

    final WsNodeHeartBeatHandler wsNodeHeartBeatHandler = new WsNodeHeartBeatHandler();

    public PeerSender(WsNode wsNode) {


        this.wsNode = wsNode;

        b = new Bootstrap();

        g = new NioEventLoopGroup();


    }

    GenericFutureListener<ChannelFuture> closeListener = (ChannelFuture f) ->
    {
        log.info("分布式连接已经断开……{"+wsNode.toString()+"}");
        channel = null;
        connectFlag = false;
    };

    private GenericFutureListener<ChannelFuture> connectedListener =  (ChannelFuture future)->{
        final EventLoop eventLoop = future.channel().eventLoop();

        if (!future.isSuccess() && ++reConnectCount < 3) {
            log.info("连接失败! 在10s之后准备尝试第{"+reConnectCount+"}次重连!");
            eventLoop.schedule(()->
                PeerSender.this.doConnect(),10, TimeUnit.SECONDS
            );
            connectFlag = false;
        } else {
            connectFlag = true;

            log.info(new Date() + "分布式节点连接成功:{"+wsNode.toString()+"}");
            channel = future.channel();
            channel.closeFuture().addListener(closeListener);

            /**
             * 发送连接成功的通知
             * */
            Notification<WsNode> notification
                    = new Notification<>(NettyWorker.getInstance().getLocalNodeInfo());
            notification.setType(Notification.CONNECT_FINISHED);
            String content = JsonUtil.pojoToJson(notification);

            MessageInfo.Model model = NotificationMsgBuilder.buildNotification(content);

            writeAndFlush(model);

        }
    };


    public void doConnect() {

        String host = wsNode.getHost();

        int port = wsNode.getPort();

        try {

            b.group(g)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .remoteAddress(host,port)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();


                            pipeline.addLast(new ProtoCodec());


                            pipeline.addLast(wsNodeHeartBeatHandler);
                        }
                    });
            log.info(new Date() + "开始连接分布式节点:{"+wsNode.toString()+"}");

            ChannelFuture f = b.connect();

            f.addListener(connectedListener);
        } catch (Exception e) {
            log.info("客户端连接失败 " + e.getMessage());
        }


    }

    public void stopConnecting() {
        g.shutdownGracefully();
        connectFlag = false;
    }

    public void writeAndFlush(Object pkg) {

            if (connectFlag == false) {
                log.info("分布式节点未连接" + wsNode.toString());
                return;
            }

            channel.writeAndFlush(pkg);
    }

}
