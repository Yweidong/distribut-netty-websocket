package com.thought.monkey.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.java.Log;
import com.thought.monkey.protoBuilder.NodeHeartBeatMsgBuilder;
import com.thought.monkey.wsmessage.MessageInfo;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 */
@Log
@ChannelHandler.Sharable
public class WsNodeHeartBeatHandler extends ChannelInboundHandlerAdapter {

    //心跳的时间间隔 默认秒
    private static final  int HEARTBEAT_INTERVAL = 50;


    /**
     * handler加入到pipeline时，开始发送心跳
     * */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        heartBeat(ctx);
    }


    //使用定时器发送心跳包
    private void heartBeat(ChannelHandlerContext ctx) {

        MessageInfo.Model model = NodeHeartBeatMsgBuilder.buildNodeHeartBeat();

        ctx.executor().schedule(()->{
            if (ctx.channel().isActive()) {

                log.info(" 发送 Node HEART_BEAT 消息");
                ctx.writeAndFlush(model);

                //递归调用，发送下一次心跳
                heartBeat(ctx);
            }
        },HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }

    /**
     * 接受到服务器的心跳包
     * */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


        //判断消息实例
        if (msg == null || !(msg instanceof MessageInfo.Model)) {
            super.channelRead(ctx, msg);
            return;
        }

        MessageInfo.Model pkg = (MessageInfo.Model) msg;

        MessageInfo.CmdType cmd = pkg.getCmd();
        if (cmd.equals(MessageInfo.CmdType.WEBSOCKET_HEARBEAT)) {
            log.info("收到 WsNode HEART_BEAT 消息");
            return;
        }


        super.channelRead(ctx, msg);
    }
}
