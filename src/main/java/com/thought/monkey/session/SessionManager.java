package com.thought.monkey.session;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import jdk.nashorn.internal.ir.CallNode;
import lombok.extern.java.Log;
import org.apache.commons.lang.StringUtils;
import com.thought.monkey.entity.*;
import com.thought.monkey.server.distributed.NettyWorker;
import com.thought.monkey.server.distributed.NodeRouter;
import com.thought.monkey.service.RedisCacheDAO;
import com.thought.monkey.service.SessionCacheDAO;
import com.thought.monkey.service.UserCacheDAO;
import com.thought.monkey.service.impl.RedisCacheImpl;
import com.thought.monkey.service.impl.SessionCacheImpl;
import com.thought.monkey.service.impl.UserCacheImpl;
import com.thought.monkey.util.JsonUtil;

import java.nio.channels.Channel;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 */
@Log
public class SessionManager {



    /*会话清单: 含本地会话、远程会话*/

    private ConcurrentHashMap<String, ServerSession> sessionMap = new ConcurrentHashMap();


    private RedisCacheDAO redisCacheDAO = new RedisCacheImpl();

    private SessionCacheDAO sessionCacheDAO = new SessionCacheImpl();

    private UserCacheDAO userCacheDAO = new UserCacheImpl();

    public SessionManager() {
    }


    /**
     *
     * 静态内部类单例
     * */

    private static class SingletonHolder{
        private static final SessionManager instance = new SessionManager();
    }

    public static SessionManager getInstance() {
        return SingletonHolder.instance;
    }

    /**
     *
     * 线程间唯一 单例
     * */
//    private static final ConcurrentHashMap<Long,SessionManager> instances
//            = new ConcurrentHashMap<>();
//
//    public static SessionManager getInstance() {
//        long id = Thread.currentThread().getId();
//        log.info("sessionManager ==  id ==" + String.valueOf(id));
//        instances.putIfAbsent(id, new SessionManager());
//        return instances.get(id);
//    }

    /**
     * 将用户和通道的关系保存到redis中
     * */
    public void add(LocalSession localSession) {


        String userid = localSession.getUserid();

        //保存本地缓存session
       String sessionId = localSession.getSessionId();
       //本地会话存储到当前netty节点的本地缓存中
        sessionMap.put(sessionId,localSession);

//        UserChannelCache userChannelCache =
//                new UserChannelCache(userid,  sessionId);


        //将用户对应的节点信息保存到reids中
        WsNode wsNode = NettyWorker.getInstance().getLocalNodeInfo();

        SessionNodeCache sessionNodeCache = new SessionNodeCache(userid, sessionId, wsNode);

        sessionCacheDAO.save(sessionNodeCache);



        //增加用户的信息 到缓存
        userCacheDAO.addSession(userid,sessionNodeCache);


//        redisCacheDAO.save(userChannelCache);

        //增加用户数，
        NettyWorker.getInstance().incBalance();



        //通知其他的netty节点

        notifyOtherWsNodeOnLine(sessionNodeCache);




    }


    /**
     * 通知其他节点有会话上线
     * todo
     * */

    private void notifyOtherWsNodeOnLine(SessionNodeCache sessionNodeCache) {


        Notification<Notification.NodeCacheWrapper> notification
                = Notification.wrapNode(sessionNodeCache);

        notification.setType(Notification.SESSION_ON);

        NodeRouter.getInstance().sendNotification(JsonUtil.pojoToJson(notification));


    }

    /**
     * 根据用户ID 获取Session 对象  （ 单机版）
     *
     * */
//    public LocalSession getSession(String userid) {

//        UserChannelCache userChannelCache = redisCacheDAO.get(userid);
//
//        if (userChannelCache ==null) return null;
//
//        System.out.println("get=="+ userChannelCache.getSessionId());
//
//        System.out.println(sessionMap.containsKey(userChannelCache.getSessionId()));
//
//        return sessionMap.get(userChannelCache.getSessionId());



//    }

    /**
     * 根据用户ID 获取Session 对象  （ 集群版）
     * */
    public List<ServerSession>  getSessionByUserId(String userid) {
        UserCache userCache = userCacheDAO.get(userid);
        if (userCache == null) {
            log.info("用户ID为 「  "+userid+" 」 下线了");
            return null;
        }

        Map<String, SessionNodeCache> allSession = userCache.getMap();
        if (allSession == null || allSession.size() == 0 ) {
            log.info("用户ID为 「  "+userid+" 」 下线了");
            return null;
        }

        List<ServerSession> sessions = new LinkedList<>();

        allSession.values().stream().forEach(sessionNodeCache -> {
            String sessionid = sessionNodeCache.getSessionid();
            //获取本地的 session
            ServerSession session = sessionMap.get(sessionid);

            //如果本地缓存没有，创建远程节点的session ，加入会话
            if (session == null) {

                session = new RemoteSession(sessionNodeCache);

                sessionMap.put(sessionid,session);
            }

            sessions.add(session);
        });

        return sessions;

    }
    /**
     * 关闭链接
     * */

    public void closeSession(ChannelHandlerContext ctx) {


        LocalSession localSession = LocalSession.getSession(ctx);


        if (localSession == null && !(localSession.isValid()))  {

            log.info("====uid is not get====");
            return;
        }



        localSession.close();

        //删除缓存中的信息
        this.remove(localSession.getSessionId());


        /**
         * 通知其他节点用户下线
         * */
        notifyOtherWsNodeOffLine(localSession);

    }

    private void notifyOtherWsNodeOffLine(LocalSession localSession) {

        if (localSession == null) {
            log.info("[SessionManager ] - file - [notifyOtherWsNodeOffLine] session is null");
            return;
        }

        Notification<Notification.ContentWrapper> notification
                = Notification.wrapContent(localSession.getSessionId());
        notification.setType(Notification.SESSION_OFF);

        NodeRouter.getInstance().sendNotification(JsonUtil.pojoToJson(notification));

    }

    /**
     *
     * 远程用户下线
     * */
    public void removeRemoteSession(String sessionId) {
        if (!sessionMap.contains(sessionId)) return;
        log.info("远程用户 --- ["+sessionId+"] --- 下线");
        sessionMap.remove(sessionId);
    }

    /**
     *
     * 远程用户上线
     *
     * @param sessionNodeCache 远程客户端的信息
     *
     * */

    public void addRemoteSession(SessionNodeCache sessionNodeCache) {
        if (sessionMap.contains(sessionNodeCache.getSessionid())) return;

        ServerSession session = new RemoteSession(sessionNodeCache);

        //保存远程节点的客户端信息到本地中
        sessionMap.put(sessionNodeCache.getSessionid(),session);
    }



    public void remove(String sessionId) {



        ServerSession session = sessionMap.get(sessionId);
        if (session == null) {

            return;
        }



        String uid = session.getUserIdValue();




        userCacheDAO.removeSession(uid,sessionId);

        //删除缓存session
        sessionCacheDAO.remove(sessionId);

        //本地：从本地会话就集合中删除会话
        sessionMap.remove(sessionId);

        NettyWorker.getInstance().decrBalance();
    }

    /**
     *
     * 根据用户ID判断用户是否在线
     * */
//    public Lo getUserOnline(String userid) {
//        UserChannelCache userChannelCache = redisCacheDAO.get(userid);
//        if (userChannelCache == null) {
//            return  null;
//        }
//        return userChannelCache;
//    }

}
