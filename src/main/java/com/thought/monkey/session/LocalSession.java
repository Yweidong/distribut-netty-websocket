package com.thought.monkey.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.java.Log;

import java.util.UUID;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 */
@Log
public class LocalSession implements ServerSession{

    /**
     * AttributeKey的实现跟ChannelOption是很相似的，
     * 相当于是一个常量池，实现了Constant接口，可以通过name和id来进行区分，
     * 并没有什么特殊的实际意义，就像一个区分的标志。
     * */
    public static AttributeKey<Boolean> IS_ACTIVE = AttributeKey.newInstance("active");

    public static AttributeKey<String> USER_ID = AttributeKey.newInstance("userId");

    public static final AttributeKey<LocalSession> SESSION_KEY =
            AttributeKey.valueOf("SESSION_KEY");

    private Channel channel;

    private String userid; //登录用户ID

    private String sessionId; //session 唯一ID

    public LocalSession(Channel channel) {
        this.channel = channel;
        this.sessionId = buildNewSessionId();
    }

    //判断用户是否为空
    @Override
    public boolean isValid() {
        return userid != null ?true:false;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    private static String buildNewSessionId() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public  boolean isActive(ChannelHandlerContext  ctx) {

        Channel channel = ctx.channel();

        return channel.attr(IS_ACTIVE).get();

    }


    public String getUserid() {
        return userid;
    }

    public Channel getChannel() {
        return channel;
    }

    public static LocalSession getSession(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        return channel.attr(SESSION_KEY).get();
    }

    public LocalSession bind() {
        System.out.println(channel.id());
        channel.attr(USER_ID).set(userid);
        channel.attr(IS_ACTIVE).set(true);
        channel.attr(SESSION_KEY).set(this);
        return this;
    }

    @Override
    public synchronized void writeAndFlush(Object pkg) {

        if (channel.isWritable()) {
            channel.writeAndFlush(pkg);
        }

    }

    @Override
    public String getUserIdValue() {
        return userid;
    }

    //关闭连接
    public synchronized  void close() {


        ChannelFuture future = channel.close();

        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    log.info("CHANNEL_CLOSED error ");
                }
            }
        });

    }




}
