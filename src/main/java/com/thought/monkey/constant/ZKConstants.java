package com.thought.monkey.constant;

import io.netty.util.AttributeKey;

public interface ZKConstants {
    String MAIN_PATH = "/nodes";

    String Temporary_NODE_PATH = MAIN_PATH + "/seq-";


    AttributeKey<String> CHANNEL_NAME =
            AttributeKey.valueOf("CHANNEL_NAME");
}
