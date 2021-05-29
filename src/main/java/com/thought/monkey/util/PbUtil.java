package com.thought.monkey.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

/**
 * @Auther: wei_tung
 * @Date: 2021/5/18 10:18
 * @Description: 1371690483@qq.com
 *
 *
 *  https://www.cnblogs.com/fnlingnzb-learner/p/13434849.html
 *
 *  对于一般的数据类型，如int,double,float,long,string都能够按照理想的方式进行转化。
 *  对于protobuf中的enum类型字段，会被按照enum的名称转化为string。
 *  对于bytes类型的字段，则会转化为utf8类型的字符串。
 */
public class PbUtil {


    /**
     * pb ---> json
     * */

    public static String pbTojson(Message sourceMessage) throws InvalidProtocolBufferException {
        String print = JsonFormat.printer().print(sourceMessage);

        return print;
    }


    /**
     * json  ----> pb
     * */

    public static Message jsonTopb(Message.Builder targetBuilder,String json) throws InvalidProtocolBufferException {

        JsonFormat.parser().merge(json,targetBuilder);

        return targetBuilder.build();
    }

}
