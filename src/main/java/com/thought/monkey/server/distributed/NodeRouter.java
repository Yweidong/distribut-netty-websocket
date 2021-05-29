package com.thought.monkey.server.distributed;

import lombok.Data;
import lombok.extern.java.Log;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import com.thought.monkey.constant.ZKConstants;
import com.thought.monkey.entity.Notification;
import com.thought.monkey.entity.WsNode;
import com.thought.monkey.pool.CuratorZKclient;
import com.thought.monkey.protoBuilder.NotificationMsgBuilder;
import com.thought.monkey.util.JsonUtil;
import com.thought.monkey.util.ObjectUtil;
import com.thought.monkey.util.ThreadUtil;
import com.thought.monkey.wsmessage.MessageInfo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 *
 * 节点路由，，监听节点的添加 ，删除事件，保存/删除远程节点的信息
 */
@Data
@Log
public class NodeRouter {

    private CuratorFramework client = null;

    private String pathRegistered = null;

    private WsNode wsNode = null;

    private boolean inited = false;

    //根节点
    private static final String path = ZKConstants.MAIN_PATH;

    /**
     * 保存节点上的所有的netty节点
     * */
    private ConcurrentHashMap<Long,PeerSender> workerMap =
            new ConcurrentHashMap<>();

    //函数式   消费
    private BiConsumer<WsNode, PeerSender> runAfterAdd = (node, relaySender) -> {
        doAfterAdd(node, relaySender);
    };


    private void doAfterAdd(WsNode node, PeerSender relaySender) {

        if (relaySender != null) {
            //关闭老连接
            relaySender.stopConnecting();
        }

        //创建一个消息转发器
        PeerSender peerSender = new PeerSender(node);
        //建立新的转发连接
        peerSender.doConnect();


        workerMap.put(node.getId(),peerSender); //将当前节点放到缓存中



    }

    private Consumer<WsNode> runAfterRemove = (node) -> {
        doAfterRemove(node);
    };

    private void doAfterRemove(WsNode node) {

        PeerSender peerSender = workerMap.get(node.getId());

        if (peerSender != null) {
            peerSender.stopConnecting();;
            workerMap.remove(node.getId());
        }


    }


    public NodeRouter() {
    }

    /**
     *
     * 静态内部类单例
     * */

    private static class SingletonHolder{
        private static final NodeRouter singleInstance = new NodeRouter();

    }

    public static NodeRouter getInstance() {

        return NodeRouter.SingletonHolder.singleInstance;
    }


    /**
     * 初始化节点，监听
     *
     * */
    public synchronized void init() {
        if (inited) return;
        inited = true;

        try {
            if (client ==null) {
                this.client = CuratorZKclient.client;
            }

            /**
             * 订阅节点的增加，删除事件
             * */
            //监听一个节点下子节点的创建、删除、更新操作
            PathChildrenCache childrenCache = new PathChildrenCache(client, path, true);
            PathChildrenCacheListener childrenCacheListener = new PathChildrenCacheListener() {

                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    log.info("filePath = [NodeRouter] = 开始监听其他的 Netty 节点" + Thread.currentThread().getName());
                    ChildData data = event.getData();
                    switch (event.getType()) {
                        //监听到节点的添加
                        case CHILD_ADDED:
                            log.info("filePath = [NodeRouter] = CHILD_ADDED : " + data.getPath() + "  数据:" + data.getData());

                            processNodeAdd(data);

                            break;
                            //节点删除事件
                        case CHILD_REMOVED:
                            log.info("filePath = [NodeRouter] = CHILD_REMOVED : " + data.getPath() + "  数据:" + data.getData());
                            processNodeRemove(data);

                            break;
                            //节点更新事件
                        case CHILD_UPDATED:
                            log.info("filePath = [NodeRouter] = CHILD_UPDATED : " + data.getPath() + "  数据:" + new String(data.getData()));



                            break;
                        default:
                            log.info("filePath = [NodeRouter] = [PathChildrenCache]节点数据为空, path={"+data == null ? "null" : data.getPath()+"}");

                            break;
                    }


                }
            };

            childrenCache.getListenable().addListener(
                    childrenCacheListener,
                    ThreadUtil.getCpuIntenseTargetThreadPool()
            );
            log.info("Register zk watcher successfully...");

            childrenCache.start(PathChildrenCache.StartMode.NORMAL);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    private void processNodeRemove(ChildData data) {

        byte[] payload = data.getData();

        wsNode = JsonUtil.jsonBytes2Object(payload, WsNode.class);

        long id = NettyWorker.getInstance().getIdByPath(data.getPath());

        wsNode.setId(id);

        log.info("filePath = [NodeRouter] = [PathChildrenCache] 节点删除，path = "+data.getPath()+ "data = "
                + JsonUtil.pojoToJson(wsNode));

        if (runAfterRemove != null) {
            runAfterRemove.accept(wsNode);
        }

    }

    /**
     * 节点增加的处理
     * */
    private void processNodeAdd(ChildData data) {

        byte[] payload = data.getData();//获取到节点的数据

//        WsNode wsNode = ObjectUtil.JsonBytes2Object(payload, WsNode.class);
         wsNode = JsonUtil.jsonBytes2Object(payload, WsNode.class);
        //获取节点的路径/根位置
        long id = NettyWorker.getInstance().getIdByPath(data.getPath());

        this.wsNode.setId(id);

        log.info("filePath = [NodeRouter] = [PathChildrenCache] 节点更新端口，path = "+data.getPath()+ "data = "
                + JsonUtil.pojoToJson(this.wsNode));

        //如果节点是当前服务的节点，就返回不作处理
        if (this.wsNode.equals(getLocalNode())) {

            log.info("filePath = [NodeRouter] = [PathChildrenCache] 本地节点，path = "+data.getPath()+ "data = "
                    + JsonUtil.pojoToJson(this.wsNode));

            return;
        }

        PeerSender peerSender = workerMap.get(this.wsNode.getId());


        //如果收到重复注册的事件也不做处理，直接返回
        if (peerSender != null && peerSender.getWsNode().equals(this.wsNode)) {
            log.info("filePath = [NodeRouter] = [PathChildrenCache] 节点重复添加，path = "+data.getPath()+ "data = "
                    + JsonUtil.pojoToJson(this.wsNode));
            return;
        }

        //后续的处理
        if (runAfterAdd != null) {
            runAfterAdd.accept(this.wsNode,peerSender);
        }


    }

    private WsNode getLocalNode() {
        return NettyWorker.getInstance().getLocalNodeInfo();
    }


    /**
     * 选择相应的转发器
     * */
    public PeerSender route(long nodeId) {



        PeerSender peerSender = workerMap.get(nodeId);
        if (peerSender != null) {
            return peerSender;
        }
        return null;
    }


    public void sendNotification(String content) {
        workerMap.keySet().stream().forEach(key->{
            if (!key.equals(getLocalNode().getId())) {
                PeerSender peerSender = workerMap.get(key);

                MessageInfo.Model model = NotificationMsgBuilder.buildNotification(content);

                peerSender.writeAndFlush(
                        model
                );
            }
         });
    }


}
