package com.thought.monkey.util;

/**
 * @Auther: wei_tung
 * @Date: 2021/4/29 15:43
 * @Description: 1371690483@qq.com
 */
public interface CallbackTask<R> {
    R execute() throws Exception;

    void onBack(R r);

    void onException(Throwable t);
}
