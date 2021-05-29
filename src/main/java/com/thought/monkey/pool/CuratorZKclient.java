package com.thought.monkey.pool;

import lombok.Data;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ResourceBundle;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 */
@Data
public class CuratorZKclient {



    public static final CuratorFramework client;

    static {
        ResourceBundle bundle = ResourceBundle.getBundle("zookeeper");
        if (bundle == null) {
            throw new IllegalArgumentException("[zookeeper.properties] is not found");
        }

        /**
         * baseSleepTimeMs :初始化Sleep时间
         *
         * maxRetries : 最大重试次数
         *
         * maxSleepMs: 最大Sleep时间 默认（2147483647）
         * */

        RetryPolicy retryPolicy
                = new ExponentialBackoffRetry(
                        Integer.valueOf(bundle.getString("zookeeper.baseSleepTimeMs")),
                Integer.valueOf(bundle.getString("zookeeper.maxRetries"))
        );

        /**
         *
         * connectString : ZooKeeper服务地址(String，多个用逗号分隔)
         *  retryPolicy : 重试策略
         *  sessionTimeoutMs  : 会话超时时间 单位ms 默认值 60000
         *
         *  connectionTimeoutMs : 连接超时时间(单位：ms  默认值：15000)
         *
         *  为了实现不同的Zookeeper业务之间的隔离，需要为每个业务分配一个独立的命名空间（namespace），
         *  即指定一个Zookeeper的根路径
         * */

        client = CuratorFrameworkFactory.builder()
                            .connectString(bundle.getString("zookeeper.ip"))
                            .sessionTimeoutMs(Integer.valueOf(bundle.getString("zookeeper.sessionTimeoutMs")))
                            .connectionTimeoutMs(Integer.valueOf(bundle.getString("zookeeper.connectionTimeoutMs")))
                            .retryPolicy(retryPolicy)
                            .namespace(bundle.getString("zookeeper.namespace"))
                            .build();
        client.start();

    }








    
}
