package com.thought.monkey.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.java.Log;
import com.thought.monkey.entity.UserChannelCache;
import com.thought.monkey.processer.MsgProcesser;
import com.thought.monkey.processer.ServerProcesser;
import com.thought.monkey.session.LocalSession;
import com.thought.monkey.session.ServerSession;
import com.thought.monkey.session.SessionManager;
import com.thought.monkey.util.FutureTaskScheduler;
import com.thought.monkey.wsmessage.MessageInfo;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 */
@Log

public class BinaryHandler extends ChannelInboundHandlerAdapter {

    private ServerProcesser msgProcesser = new MsgProcesser();



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.info("二进制流。。。");
            //判断消息实例
            if (null == msg || !(msg instanceof MessageInfo.Model))
            {
                super.channelRead(ctx, msg);
                return;
            }
            MessageInfo.Model model = (MessageInfo.Model) msg;

            if (!model.getCmd().equals(msgProcesser.op())) {
                super.channelRead(ctx,msg);
                return;
            }
            //异步处理消息
            FutureTaskScheduler.add(()->{


                LocalSession session = LocalSession.getSession(ctx);

                log.info("BinaryHandler file ==>" + session);

                //如果当前用户是在线状态
                if (session!= null && session.isActive(ctx)) {

                    msgProcesser.action(session,model);
                    return;
                }


                String to = model.getTo();


                //判断接受人是否在线 针对远程netty服务器上的用户

                List<ServerSession> toSessions = SessionManager.getInstance().getSessionByUserId(to);


                if (toSessions != null) {


                    toSessions.forEach(serverSession -> {

                        serverSession.writeAndFlush(model);
                    });
                } else {

                    //将数据保存到存储介质中 todo
                }

            });




    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("close");
        SessionManager.getInstance().closeSession(ctx);
    }
}
