package com.thought.monkey.codec.ws;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.java.Log;
import com.thought.monkey.server.distributed.NettyWorker;
import com.thought.monkey.session.SessionManager;
import com.thought.monkey.wsmessage.MessageInfo;

import java.awt.*;
import java.util.List;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 */

@Log
public class FrameCodec extends MessageToMessageCodec<WebSocketFrame, MessageInfo.Model> {

    private WebSocketServerHandshaker handshaker;
    public FrameCodec( WebSocketServerHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageInfo.Model model, List<Object> list) throws Exception {

        byte[] bytes = model.toByteArray();

        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);

        /**
         * 将bytebuf转换成websocket二进制流，，客户端不能直接解析protobuf编码生成的
         * */
        BinaryWebSocketFrame binaryWebSocketFrame = new BinaryWebSocketFrame(byteBuf);

        list.add(binaryWebSocketFrame);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame webSocketFrame, List<Object> list) throws Exception {

        if (webSocketFrame instanceof BinaryWebSocketFrame) {

            ByteBuf byteBuf = ((BinaryWebSocketFrame) webSocketFrame).content();

            list.add(byteBuf);


            byteBuf.retain();

        } else if (webSocketFrame instanceof CloseWebSocketFrame) {

            log.info("========== closeWebSocketFrame ==========");

//            SessionManager.getInstance().closeSession(ctx);

            handshaker.close(ctx.channel(),((CloseWebSocketFrame) webSocketFrame).retain());

            return;

        }


    }
}
