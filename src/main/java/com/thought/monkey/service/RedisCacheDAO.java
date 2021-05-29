package com.thought.monkey.service;

import com.thought.monkey.entity.UserChannelCache;

public interface RedisCacheDAO {

    //保存用户与通道间的关系
    void save(UserChannelCache userChannelCache);

    //从redis中获取
    UserChannelCache get(String userid);

    //删除会话
    void remove(String userid);
}
