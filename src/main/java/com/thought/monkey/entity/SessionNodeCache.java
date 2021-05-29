package com.thought.monkey.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 *
 *  当前连接的信息和节点信息
 *
 */
@Data
public class SessionNodeCache implements Serializable {
    private static final long serialVersionUID = 5749422973063654486L;

    private String userid; //用户ID

    private String sessionid;//分布式唯一ID

    private WsNode wsNode;//节点信息

    public SessionNodeCache() {
        userid = "";
        sessionid = "";
        wsNode = new WsNode("unknown",0);
    }

    public SessionNodeCache(String userid, String sessionid, WsNode wsNode) {
        this.userid = userid;
        this.sessionid = sessionid;
        this.wsNode = wsNode;
    }
}
