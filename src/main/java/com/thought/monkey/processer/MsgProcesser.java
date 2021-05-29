package com.thought.monkey.processer;

import lombok.extern.java.Log;
import com.thought.monkey.entity.UserChannelCache;
import com.thought.monkey.session.LocalSession;
import com.thought.monkey.session.ServerSession;
import com.thought.monkey.session.SessionManager;
import com.thought.monkey.wsmessage.MessageInfo;

import java.util.List;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 */
@Log
public class MsgProcesser implements ServerProcesser{


    @Override
    public MessageInfo.CmdType op() {
        return MessageInfo.CmdType.WEBSOCKET_DATA;
    }

    @Override
    public Boolean action(LocalSession localSession, MessageInfo.Model model) {



        //获取接收人
        String to = model.getTo();

        //判断接受人是否在线

        List<ServerSession> toSessions = SessionManager.getInstance().getSessionByUserId(to);




        if (toSessions != null) {

            toSessions.forEach(serverSession -> {

                serverSession.writeAndFlush(model);
            });
        } else {
            //将数据保存到存储介质中


        }


        return true;
    }
}
