package com.thought.monkey.processer;

import com.thought.monkey.protoBuilder.ConnectResponseBuilder;
import com.thought.monkey.session.LocalSession;
import com.thought.monkey.session.SessionManager;
import com.thought.monkey.wsmessage.MessageInfo;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 */
public class ConnectProcesser implements ServerProcesser{





    @Override
    public MessageInfo.CmdType op() {
        return MessageInfo.CmdType.WEBSOCKET_ONLINE;
    }

    @Override
    public Boolean action(LocalSession localSession, MessageInfo.Model model) {

        /**
         * 可以验证来源用户
         * todo
         * */

        localSession.setUserid(model.getFrom());



        localSession.bind();

        /**
         * 将用户与通道进行绑定
         * */
        SessionManager.getInstance().add(localSession);

        /**
         * 通知客户端链接成功
         * */
        MessageInfo.Model response = ConnectResponseBuilder.connectResponse();

        localSession.writeAndFlush(response);

        return true;
    }
}
