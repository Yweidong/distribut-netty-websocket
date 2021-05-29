package com.thought.monkey.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.java.Log;
import com.thought.monkey.processer.ConnectProcesser;
import com.thought.monkey.processer.ServerProcesser;
import com.thought.monkey.session.LocalSession;
import com.thought.monkey.session.SessionManager;
import com.thought.monkey.util.CallbackTask;
import com.thought.monkey.util.CallbackTaskScheduler;
import com.thought.monkey.wsmessage.MessageInfo;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 */
@Log
@ChannelHandler.Sharable
public class ConnectHandler extends ChannelInboundHandlerAdapter {

    private ServerProcesser connectProcesser = new ConnectProcesser();


    private LocalSession localSession = null;


    //共享通道
    final HeartBeatHandler heartBeatHandler = new HeartBeatHandler();
//    final BinaryHandler binaryHandler = new BinaryHandler();//@Shareable

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("二进制流开始");
//

        if (null == msg || !(msg instanceof MessageInfo.Model))
        {
            super.channelRead(ctx, msg);
            return;
        }

        MessageInfo.Model model = (MessageInfo.Model) msg;

        if (model.getCmd().equals(connectProcesser.op())) {

            /**
             * 处理客户端连接到服务器后，保存用户和通道的对应关系，确保之后消息的单点/群体发送
             * */

            System.out.println(model.getCmd());
            localSession = new LocalSession(ctx.channel());

            //异步处理逻辑
            CallbackTaskScheduler.add(new CallbackTask<Boolean>() {
                @Override
                public Boolean execute() throws Exception {

                    return connectProcesser.action(localSession, model);

                }


                @Override
                public void onBack(Boolean aBoolean) {
                    if (aBoolean) {
                        log.info(" connect success");
                        //心跳检测
                        ctx.pipeline().addAfter("connectHandler","binary",new BinaryHandler());
                        ctx.pipeline().addAfter("connectHandler","heartBeat",heartBeatHandler);

                        ctx.pipeline().remove("connectHandler");
                    } else {
                        SessionManager.getInstance().closeSession(ctx);
                    }
                }
                /**
                 * 异常回调
                 * */
                @Override
                public void onException(Throwable t) {

                    t.printStackTrace();
                    SessionManager.getInstance().closeSession(ctx);
                    log.info("登录失败" + localSession.getUserid());
                }
            });



        } else {
            super.channelRead(ctx,msg);
            return;
        }
    }

    /**
     * 客户端关闭
     * */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("close");
        SessionManager.getInstance().closeSession(ctx);
    }
}
