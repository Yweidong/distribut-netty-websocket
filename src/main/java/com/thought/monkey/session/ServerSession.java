package com.thought.monkey.session;

public interface ServerSession {

    void writeAndFlush(Object pkg);

    String getSessionId();

    boolean isValid();

    /**
     * 获取用户id
     * @return  用户id
     */
    String getUserIdValue();
}
