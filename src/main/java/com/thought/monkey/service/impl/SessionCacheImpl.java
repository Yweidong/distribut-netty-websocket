package com.thought.monkey.service.impl;

import org.apache.commons.lang.StringUtils;
import com.thought.monkey.entity.SessionNodeCache;
import com.thought.monkey.helper.RedisHelper;
import com.thought.monkey.service.SessionCacheDAO;
import com.thought.monkey.util.JsonUtil;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 *
 *  保存用户节点的信息
 */
public class SessionCacheImpl implements SessionCacheDAO {

    public static final String REDIS_PREFIX = "SessionCache:id:";

    private static final int CASHE_LONG = 60 * 4 * 60;//

    @Override
    public void save(SessionNodeCache sessionNodeCache) {
        String key = REDIS_PREFIX + sessionNodeCache.getSessionid();
        String value = JsonUtil.pojoToJson(sessionNodeCache);

        RedisHelper.set(key,value,CASHE_LONG);
    }

    @Override
    public SessionNodeCache get(String sessionId) {

        String key = REDIS_PREFIX + sessionId;

        String value = RedisHelper.get(key);
        if (!StringUtils.isEmpty(value) ) {
            return JsonUtil.jsonToPojo(value,SessionNodeCache.class);
        }


        return null;
    }

    @Override
    public void remove(String sessionId) {
        String key = REDIS_PREFIX + sessionId;

        RedisHelper.remove(key);
    }
}
