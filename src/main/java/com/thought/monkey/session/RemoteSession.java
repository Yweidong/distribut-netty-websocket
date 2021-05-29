package com.thought.monkey.session;

import lombok.Data;
import lombok.extern.java.Log;
import com.thought.monkey.entity.SessionNodeCache;
import com.thought.monkey.entity.WsNode;
import com.thought.monkey.server.distributed.NodeRouter;
import com.thought.monkey.server.distributed.PeerSender;

import java.io.Serializable;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 */
@Data
@Log
public class RemoteSession implements ServerSession {


    SessionNodeCache sessionNodeCache;

    private boolean valid  = true;

    public RemoteSession(SessionNodeCache sessionNodeCache) {
        this.sessionNodeCache = sessionNodeCache;
    }

    @Override
    public void writeAndFlush(Object pkg) {
        WsNode wsNode = sessionNodeCache.getWsNode();


        long id = wsNode.getId();



        PeerSender route = NodeRouter.getInstance().route(id);

        log.info("[ RemoteSession file ] ==> " + route);

        if (route != null) {
            route.writeAndFlush(pkg);
        }
    }

    @Override
    public String getSessionId() {
        return sessionNodeCache.getSessionid();
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public String getUserIdValue() {
        return sessionNodeCache.getUserid();
    }
}
