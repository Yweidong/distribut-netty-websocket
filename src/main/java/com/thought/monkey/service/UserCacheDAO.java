package com.thought.monkey.service;

import com.thought.monkey.entity.SessionNodeCache;
import com.thought.monkey.entity.UserCache;

public interface UserCacheDAO {

    // 保持用户缓存
    void save(UserCache s);

    // 获取用户缓存
    UserCache get(String userId);

    //增加 用户的  会话
    void addSession(String uid, SessionNodeCache session);


    //删除 用户的  会话
    void removeSession(String uid, String sessionId);

}
