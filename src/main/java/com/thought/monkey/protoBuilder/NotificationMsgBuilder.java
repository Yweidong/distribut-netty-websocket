package com.thought.monkey.protoBuilder;

import com.thought.monkey.wsmessage.MessageInfo;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 */
public class NotificationMsgBuilder {

    public static MessageInfo.Model buildNotification(String content) {
         MessageInfo.MessageBody messageBody = MessageInfo.MessageBody.newBuilder()
                .setContent(content)
                .setType(0)
                .setTime(String.valueOf(System.currentTimeMillis())).build();

        MessageInfo.Model.Builder rb = MessageInfo.Model.newBuilder()
                .setCmd(MessageInfo.CmdType.MESSAGE_NOTIFICATION)
                .setContent(messageBody.toByteString());
        return rb.build();
    }
}
