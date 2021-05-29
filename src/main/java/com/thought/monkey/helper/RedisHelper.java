package com.thought.monkey.helper;

import lombok.extern.java.Log;
import com.thought.monkey.pool.RedisPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 */
@Log
public class RedisHelper extends RedisPool {


    private static Jedis jedis = null;


    public static String set(String key,String value) {

        String rtn = null;

        try {
            jedis = getJedis();
            rtn = jedis.set(key, value);

        } catch (Exception e ) {
            log.info("== redis set failed  == " + e.getMessage() );
            //释放资源
            returnResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return rtn;
    }

    public static String set(String key,String value,int timeout) {

        String rtn = null;

        try {
            jedis = getJedis();
            rtn = jedis.setex(key,timeout,value);

        } catch (Exception e ) {
            log.info("== redis set failed  == " + e.getMessage() );
            //释放资源
            returnResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return rtn;
    }


    public static String get(String key) {
            String rtn = null;

            try {
                 jedis = getJedis();
                 rtn = jedis.get(key);
            } catch (Exception e ) {
                log.info("== redis get failed  == " + e.getMessage() );
                //释放资源
                returnResource(jedis);
            } finally {
                returnResource(jedis);
            }

        return rtn;
    }

    public static void remove(String key) {
        try {
            log.info("redis del ");
             jedis = getJedis();
                jedis.del(key);
        } catch (Exception e ) {
            log.info("== redis del failed  == " + e.getMessage() );
            //释放资源
            returnResource(jedis);
        } finally {
            returnResource(jedis);
        }
    }
}
