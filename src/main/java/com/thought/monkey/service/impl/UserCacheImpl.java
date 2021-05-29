package com.thought.monkey.service.impl;

import org.apache.commons.lang.StringUtils;
import com.thought.monkey.entity.SessionNodeCache;
import com.thought.monkey.entity.UserCache;
import com.thought.monkey.helper.RedisHelper;
import com.thought.monkey.service.UserCacheDAO;
import com.thought.monkey.util.JsonUtil;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 */
public class UserCacheImpl implements UserCacheDAO {


    public static final String REDIS_PREFIX = "UserCache:uid:";

    private static final int CASHE_LONG = 60 * 4 * 60;//4小时之后，得重新登录

    @Override
    public void save(UserCache s) {
        String key = REDIS_PREFIX + s.getUserId();

        String value = JsonUtil.pojoToJson(s);

        RedisHelper.set(key,value,CASHE_LONG);
    }

    @Override
    public UserCache get(String userId) {
        String key = REDIS_PREFIX + userId;

        String value = RedisHelper.get(key);

        if (!StringUtils.isEmpty(value)) {
            return JsonUtil.jsonToPojo(value,UserCache.class);
        }


        return null;
    }

    @Override
    public void addSession(String uid, SessionNodeCache session) {
        UserCache userCache = get(uid);
        if (userCache == null) {
            userCache = new UserCache(uid);
        }

        userCache.addSession(session);
        save(userCache);//更新缓存
    }

    @Override
    public void removeSession(String uid, String sessionId) {

        UserCache userCache = get(uid);

        if (userCache == null) {
            userCache = new UserCache(uid);
        }

        userCache.removeSession(sessionId);
        save(userCache);

    }
}
