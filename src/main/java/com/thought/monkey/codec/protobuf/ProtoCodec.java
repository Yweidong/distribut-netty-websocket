package com.thought.monkey.codec.protobuf;

import com.sun.tools.javac.util.Assert;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.java.Log;
import com.thought.monkey.constant.PbConstants;
import com.thought.monkey.wsmessage.MessageInfo;

import java.util.List;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 *
 *
 ** <p>
 *  *
 *  * +----------+----------+----------------------------+
 *  * |  size    |  value   |  intro                     |
 *  * +----------+----------+----------------------------+
 *  * | 1 bytes  | 0x86     |  magic number     魔数      |
 *  * | 4 bytes  |          |  content length   数据长度   |
 *  * | ? bytes  |          |  the content      数据内容   |
 *  * +----------+----------+----------------------------+
 *  * </p>
 *
 */
@Log
public class ProtoCodec extends ByteToMessageCodec<MessageInfo.Model> {


    /**
     * 数据的编码
     * */
    @Override
    protected void encode(ChannelHandlerContext ctx, MessageInfo.Model model, ByteBuf out) throws Exception {

        out.writeByte(PbConstants.MAGIC_CODE);

        byte[] bytes = model.toByteArray(); //将对象转换为字节数组 计算长度

        int length = bytes.length;  //4个字节
        out.writeInt(length);

        out.writeBytes(bytes);//将数据最后写入
    }


    /**
     * 数据的解码
     * */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {

        in.markReaderIndex();//标记一下当前的readIndex的位置

        if (in.readableBytes() < 5) {  // 封装的数据格式头部小于5个字节，代表数据不符合规范
            return;
        }
        //读取魔数
        byte magic = in.readByte();
        Assert.check(magic == PbConstants.MAGIC_CODE,"magic number is invalid");

        int length = in.readInt(); //读取长度

        if (length < 0) {
            ctx.close(); //非法数据，关闭连接
        }


        //如果数据的长度大于可读字节的长度，就要重置读取位置
        if (length > in.readableBytes()) {
            in.resetReaderIndex();
            return;
        }

        byte[] array;

        if (in.hasArray()) {
            //堆缓冲

            ByteBuf slice = in.slice(in.readerIndex(), length);

            array = slice.array();

            in.retain();

        } else {
            array = new byte[length];

            in.readBytes(array,0,length);
        }


        //字节转换成对象

        MessageInfo.Model model = MessageInfo.Model.parseFrom(array);

        if (in.hasArray()) in.release();

        if (model != null) {

            //将业务消息添加，给后续的handler进行处理
            list.add(model);
        }


    }
}
