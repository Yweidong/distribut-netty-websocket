package com.thought.monkey.handler.protobuf;

import com.google.gson.reflect.TypeToken;
import com.google.protobuf.ByteString;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.java.Log;
import com.thought.monkey.constant.ZKConstants;
import com.thought.monkey.entity.Notification;
import com.thought.monkey.entity.SessionNodeCache;
import com.thought.monkey.entity.WsNode;
import com.thought.monkey.handler.BinaryHandler;
import com.thought.monkey.handler.HeartBeatHandler;
import com.thought.monkey.session.SessionManager;
import com.thought.monkey.util.JsonUtil;
import com.thought.monkey.util.PbUtil;
import com.thought.monkey.wsmessage.MessageInfo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 *
 * 通知类处理器
 */
@Log
@ChannelHandler.Sharable
public class RemoteNotificationHandler
                    extends ChannelInboundHandlerAdapter {

    final HeartBeatHandler heartBeatHandler = new HeartBeatHandler();

    /**
     *
     * 收到客户端的信息
     * */

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {



        if (msg == null || !(msg instanceof MessageInfo.Model)) {

            super.channelRead(ctx, msg);

            return;
        }

        MessageInfo.Model pkg = (MessageInfo.Model) msg;

        //判断类型，如果不是通知类型就跳过
        MessageInfo.CmdType cmd = pkg.getCmd();
        if (!cmd.equals(MessageInfo.CmdType.MESSAGE_NOTIFICATION)) {
            super.channelRead(ctx, msg);
            return;
        }


        //处理消息
        ByteString content = pkg.getContent();

        MessageInfo.MessageBody messageBody = MessageInfo.MessageBody.parseFrom(content);

        System.out.println(messageBody.toString());



        String json = messageBody.getContent();


        log.info("收到通知，json = {"+json+"}");
        //将字符串转换成消息对象
        Notification<Notification.ContentWrapper> notification = JsonUtil.jsonToPojo(
                json, new TypeToken<Notification<Notification.ContentWrapper>>() {
        }.getType());

        //当前消息为下线的通知
        if (notification.getType() == Notification.SESSION_OFF) {
            //获取到客户端连接的唯一ID
            String sessionid = notification.getWrapContent();
            log.info("收到用户{ "+sessionid+"} 下线通知");

            SessionManager.getInstance().removeRemoteSession(sessionid);

        }

        //当前消息为上线的通知
        if (notification.getType() == Notification.SESSION_ON) {
            //获取到远程节点客户端的信息
            SessionNodeCache sessionNodeCache = notification.getWrapNode();

//            //获取到客户端连接的唯一ID
//            String sessionid = notification.getWrapContent();
            log.info("收到用户{ "+sessionNodeCache.getSessionid()+"} 上线通知");



            SessionManager.getInstance().addRemoteSession(sessionNodeCache);

        }
        System.out.println(notification.getType());
        //节点链接成功的通知
        if (notification.getType() == Notification.CONNECT_FINISHED) {

            Notification<WsNode> nodeInfo
                    = JsonUtil.jsonToPojo(
                            json,
                    new TypeToken<Notification<WsNode>>(){}.getType()
            );

            ctx.pipeline().addLast(heartBeatHandler);
            ctx.pipeline().addLast(new BinaryHandler());
            log.info("收到分布式节点连接成功通知, node={"+json+"}");

            ctx.channel().attr(ZKConstants.CHANNEL_NAME).set(JsonUtil.pojoToJson(nodeInfo));
        }

    }
}
