package com.thought.monkey.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import io.netty.channel.Channel;
import com.thought.monkey.entity.UserChannelCache;
import com.thought.monkey.helper.RedisHelper;
import com.thought.monkey.service.RedisCacheDAO;
import com.thought.monkey.util.JsonUtil;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 * 单机版 netty 就用这个足够
 */
public class RedisCacheImpl implements RedisCacheDAO {

    private static final String REDIS_ONLINE_KEY = "online:id:";





    private static final int CASHE_LONG = 60 * 4 * 60;//4小时之后，得重新登录

    @Override
    public void save(UserChannelCache userChannelCache) {
        String key = REDIS_ONLINE_KEY + userChannelCache.getUserid();

        String value = JSON.toJSONString(userChannelCache);

        RedisHelper.set(key,value,CASHE_LONG);
    }

    @Override
    public UserChannelCache get(String userid) {
        String key = REDIS_ONLINE_KEY + userid;
        String s = RedisHelper.get(key);


        if (s != null) {

//            System.out.println("json " + s);
//
            UserChannelCache userChannelCache = JSON.parseObject(s,UserChannelCache.class);

            return userChannelCache;
        }
        return null;
    }

    @Override
    public void remove(String userid) {
        String key  = REDIS_ONLINE_KEY + userid;
        RedisHelper.remove(key);

    }
}
