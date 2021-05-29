package com.thought.monkey.entity;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

import java.io.Serializable;


/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 */
public class UserChannelCache implements Serializable {


    private static final long serialVersionUID = -2329023296822932956L;

    private String userid;

    private String sessionId;



    public UserChannelCache(String userid, String sessionId) {
        this.userid = userid;
        this.sessionId = sessionId;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "UserChannelCache{" +
                "userid='" + userid + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
