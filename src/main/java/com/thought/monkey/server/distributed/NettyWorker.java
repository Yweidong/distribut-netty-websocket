package com.thought.monkey.server.distributed;

import lombok.Data;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import com.thought.monkey.constant.ZKConstants;
import com.thought.monkey.entity.WsNode;
import com.thought.monkey.pool.CuratorZKclient;
import com.thought.monkey.session.SessionManager;
import com.thought.monkey.util.JsonUtil;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 *
 *
 *
 *
 *
 *              PERSISTENT：持久化(默认)
 *              PERSISTENT_SEQUENTIAL：持久化并且带序列号
 *              EPHEMERAL：临时
 *              EPHEMERAL_SEQUENTIAL：临时并且带序列号
 */
@Data
@Log
public class NettyWorker {

    //zK curator 客户端
    private CuratorFramework  client;


    //netty的节点信息
    private WsNode wsNode = new WsNode();

    //保存当前netty节点的路径，创建成功后返回
    private String pathRegistered = null;


    private boolean inited;

    public NettyWorker() {
    }

    /**
     *
     * 静态内部类单例
     * */

    private static class SingletonHolder{
        private static final NettyWorker singleInstance = new NettyWorker();

    }

    public static NettyWorker getInstance() {
//        SingletonHolder.singleInstance.wsNode = new WsNode();

        return SingletonHolder.singleInstance;
    }


    //在zookeeper中创建临时节点
    public synchronized void init() {
        if (inited) return;

        inited = true;

        //初始化zk客户端
        if (client == null) {
            this.client = CuratorZKclient.client;

        }

        //初始化netty节点
        if (wsNode == null) {

            wsNode = new WsNode();
        }

        //创建父节点
        createParentIfNeeded(ZKConstants.MAIN_PATH);


        //创建字节点   节点的payload 为当前worker 实例

        try {
            System.out.println(wsNode.getPort());
            byte[] payload = JsonUtil.object2JsonBytes(wsNode);

            pathRegistered = client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)  //临时并且带序列号
                    .forPath(ZKConstants.Temporary_NODE_PATH, payload);

            //给当前节点对象添加 ID
            wsNode.setId(getId());
            log.info("本地节点，path={ "+pathRegistered+"},  id ={"+ wsNode.getId()+ "}");
        } catch (Exception e) {
            e.printStackTrace();
        }




    }

    public void setWsNode(String ip ,int port) {
       wsNode.setHost(ip);
       wsNode.setPort(port);
    }

    /**
     * 获取节点的编号
     * */
    private long getId() {
        return getIdByPath(pathRegistered);
    }


    /**
     * @param pathRegistered  路径
     * */
    public long getIdByPath(String pathRegistered) {
        String sid = null;
        if (pathRegistered == null) {
            throw new RuntimeException("节点路径错误");
        }

        int index = pathRegistered.lastIndexOf(ZKConstants.Temporary_NODE_PATH);
//        log.info("zk index ==== " + index);
        if (index >= 0 ) {
            index += ZKConstants.Temporary_NODE_PATH.length();
//            log.info("zk value ==== " + index);
            sid = index <= pathRegistered.length() ? pathRegistered.substring(index) : null;
//            log.info("sid value ==== " + index);
        }
        if (null == sid) {
            throw new RuntimeException("节点ID获取失败");
        }

        return Long.parseLong(sid);

    }


    private void createParentIfNeeded(String mainPath) {

        try {

            //检查节点是否存在
            Stat stat = client.checkExists().forPath(mainPath);
            if (stat == null) {
                client.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT) //父节点可以设置永久
                        .forPath(mainPath);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 增加负载，表示用户登录成功
     * */
    public synchronized boolean incBalance() {

        if (null == wsNode) throw new RuntimeException("还未设置WsNode 节点");

        while (true) {
            try {
                wsNode.incrementBalance();
                byte[] payload = JsonUtil.object2JsonBytes(wsNode);

                //写回到zookeeper
                client.setData().forPath(pathRegistered,payload);
                return true;

            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * 减少负载，表示用户下线
     * */
    public synchronized boolean decrBalance() {
        if (null == wsNode) throw new RuntimeException("还未设置WsNode 节点");
        while (true) {
            try {

                wsNode.decrementBalance();

                byte[] payload = JsonUtil.object2JsonBytes(wsNode);

                //写回到zookeeper
                client.setData().forPath(pathRegistered,payload);
                return true;

            } catch (Exception e) {
                return false;
            }
        }
    }




    /**
     * 返回本地节点的信息
     * */

    public WsNode getLocalNodeInfo() {
        return wsNode;
    }

}
