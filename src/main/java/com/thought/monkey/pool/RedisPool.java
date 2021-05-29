package com.thought.monkey.pool;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.ResourceBundle;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 */
public class RedisPool {

    private static JedisPool jedisPool = null;

    static {
        ResourceBundle bundle = ResourceBundle.getBundle("redis");
        if (bundle == null) {
            throw new IllegalArgumentException("[redis.properties] is not found");
        }


        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(Integer.valueOf(bundle.getString("redis.pool.maxIdle")));
        poolConfig.setMaxTotal(Integer.valueOf(bundle.getString("redis.pool.maxActive")));
        poolConfig.setMaxWaitMillis(Integer.valueOf(bundle.getString("redis.pool.maxWait")));

        //在borrow(用)一个jedis实例时，是否提前进行validate(验证)操作；
        //如果为true，则得到的jedis实例均是可用的
        poolConfig.setTestOnBorrow(Boolean.valueOf(bundle.getString("redis.pool.testOnBorrow")));

        poolConfig.setTestOnReturn(Boolean.valueOf(bundle.getString("redis.pool.testOnReturn")));

        jedisPool = new JedisPool(
                poolConfig,
                bundle.getString("redis.ip"),
                Integer.valueOf(bundle.getString("redis.port")),
                Integer.valueOf(bundle.getString("redis.timeout")),
                bundle.getString("redis.password"),
                Integer.valueOf(bundle.getString("redis.database"))
        );
    }

    /**
     * 获取jedis实例
     * */
    public synchronized static Jedis getJedis() {
        try {
            if (jedisPool != null ) {
                Jedis jedis = jedisPool.getResource();
                return jedis;
            } else {
                return null;
            }
        } catch (JedisConnectionException e) {
            return null;
        }
    }

    public static void returnResource(final Jedis jedis){
        //方法参数被声明为final，表示它是只读的。
        if(jedis!=null){
           jedis.close();

        }
    }

    public static void main(String[] args) {
        RedisPool.getJedis().set("ceshi", String.valueOf(111));
    }

}
