package com.thought.monkey.service;

import com.thought.monkey.entity.SessionNodeCache;

public interface SessionCacheDAO {


    void save(SessionNodeCache sessionNodeCache);

    SessionNodeCache get(String sessionId);

    void remove(String sessionId);

}
