package com.thought.monkey.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.java.Log;
import com.thought.monkey.util.FutureTaskScheduler;
import com.thought.monkey.wsmessage.MessageInfo;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 *
 * 心跳包
 */
@Log
@ChannelHandler.Sharable
public class HeartBeatHandler extends IdleStateHandler {

    public HeartBeatHandler() {
        super(100,0,0, TimeUnit.SECONDS);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {



        if (msg instanceof MessageInfo.Model) {

            MessageInfo.Model message = (MessageInfo.Model) msg;
            log.info("----接收心跳包----");
            if (message.getCmd() == MessageInfo.CmdType.WEBSOCKET_HEARBEAT) {
                //异步处理，将心跳包发送给客户端

                FutureTaskScheduler.add(()->{

                    if (ctx.channel().isActive()) {
                        ctx.channel().writeAndFlush(msg);
                    }

                });

            } else {
                super.channelRead(ctx, msg);//转到下一个handler执行
                return;
            }

        }
        super.channelRead(ctx, msg);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {

        /**
         *  如果检测到读空闲了，代表客户端没有数据发送过来，有可能断开 ,此时就要关闭通道，和 删除通道对应的关系
         *
         *  TODO
         * */
        if (evt == IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT) {
            ctx.close();
        }
    }
}
