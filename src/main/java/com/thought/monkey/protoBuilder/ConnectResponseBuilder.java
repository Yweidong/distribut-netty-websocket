package com.thought.monkey.protoBuilder;

import com.thought.monkey.wsmessage.MessageInfo;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 */
public class ConnectResponseBuilder {

    public static MessageInfo.Model connectResponse(

    ) {
        MessageInfo.MessageBody.Builder mb = MessageInfo.MessageBody.newBuilder()
                .setTitle("服务端")
                .setContent("客户端连接成功。。。")
                .setTime(String.valueOf(System.currentTimeMillis()))
                .setType(1)
                .setExtend("");
        MessageInfo.Model.Builder builder = MessageInfo.Model.newBuilder()
                .setContent(mb.build().toByteString());

        return builder.build();

    }
}
