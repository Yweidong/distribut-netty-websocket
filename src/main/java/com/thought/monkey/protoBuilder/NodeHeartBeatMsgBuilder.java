package com.thought.monkey.protoBuilder;

import com.thought.monkey.wsmessage.MessageInfo;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 */
public class NodeHeartBeatMsgBuilder {

    public static MessageInfo.Model buildNodeHeartBeat() {

        MessageInfo.Model.Builder mb = MessageInfo.Model.newBuilder()
                .setCmd(MessageInfo.CmdType.WEBSOCKET_HEARBEAT);
        return mb.build();
    }
}
