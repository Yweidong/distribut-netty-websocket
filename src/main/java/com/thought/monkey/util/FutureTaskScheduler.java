package com.thought.monkey.util;


import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Auther: wei_tung
 * @Date: 2021/5/5 13:44
 * @Description: 1371690483@qq.com
 *
 * 无返回值的异步任务
 */
public class FutureTaskScheduler {

    static ThreadPoolExecutor tPool = null;



    static {
        tPool = ThreadUtil.getCpuIntenseTargetThreadPool();
    }

    /**
     * 添加任务
     * */
    public static void add(Runnable runnable) {
        tPool.submit(() ->{
           runnable.run();
        });
    }
}
