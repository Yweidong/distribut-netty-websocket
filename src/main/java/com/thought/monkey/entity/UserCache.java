package com.thought.monkey.entity;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 */
@Data
public class UserCache {
    private String userId;

    /**
     * 当前用户可能登录不同的设备
     * */
    private Map<String, SessionNodeCache> map = new LinkedHashMap<>(10);

    public UserCache(String userId)
    {
        this.userId = userId;
    }

    //为用户增加session
    public void addSession(SessionNodeCache session)
    {

        map.put(session.getSessionid(), session);
    }

    //为用户移除session
    public void removeSession(String sessionId)
    {
        map.remove(sessionId);
    }
}
