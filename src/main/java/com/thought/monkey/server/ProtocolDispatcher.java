package com.thought.monkey.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

import com.thought.monkey.codec.protobuf.ProtoCodec;
import com.thought.monkey.handler.BinaryHandler;
import com.thought.monkey.handler.ConnectHandler;
import com.thought.monkey.handler.WsNodeHeartBeatHandler;
import com.thought.monkey.handler.protobuf.RemoteNotificationHandler;
import com.thought.monkey.handler.ws.HttpHandler;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 */
@Slf4j
public class ProtocolDispatcher extends ByteToMessageDecoder {


    final RemoteNotificationHandler remoteNotificationHandler = new RemoteNotificationHandler();


    final HttpHandler httpHandler = new HttpHandler();
//    final BinaryHandler binaryHandler = new BinaryHandler();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf o, List list) throws Exception {



        if (isHttp(o)) {

            dispatchToHttp(ctx,o);

        } else {
            dispatchToMessage(ctx);
        }
    }

    private boolean isHttp(ByteBuf o) {

        String s = o.toString(0, 3, Charset.forName("utf8"));

        if ("GET".equals(s)) return true;


        return false;
    }

    private void dispatchToHttp(ChannelHandlerContext ctx,Object pkg) {

        ChannelPipeline pipeline = ctx.pipeline();

        //websocket协议本身是基于http协议的，所以需要http解码器
        pipeline.addLast("http-codec",new HttpServerCodec());

        //已块的方式来写的处理器
        pipeline.addLast("http-chunked",new ChunkedWriteHandler());

        //netty是基于分段请求的，HttpObjectAggregator的作用是将请求分段再聚合,参数是聚合字节的最大长度

        pipeline.addLast("aggregator",new HttpObjectAggregator(1024 * 1024 * 1024));

        pipeline.addLast("httpHandler",httpHandler);

        pipeline.remove(this);

        ctx.fireChannelRead(pkg);


    }

    private void dispatchToMessage(ChannelHandlerContext ctx) {

        ChannelPipeline pipeline = ctx.pipeline();

        //内部protobuf数据编解码

        pipeline.addLast("codec",new ProtoCodec());


        pipeline.addLast(remoteNotificationHandler);




        pipeline.remove(this);
        ctx.fireChannelActive();

    }



}
