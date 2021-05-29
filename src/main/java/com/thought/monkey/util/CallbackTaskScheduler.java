package com.thought.monkey.util;

import com.google.common.util.concurrent.*;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Auther: wei_tung
 * @Date: 2021/4/29 15:43
 * @Description: 1371690483@qq.com
 */
public class CallbackTaskScheduler {

    /**
     *
     * MoreExecutors是对Feture的升级，
     * jdk中的Feture是异步提交任务的返回值，但要获取它的执行结果，需要调用get方法，但此方法是阻塞的。
     * 在谷歌的jar包中，提供了MoreExecutors.listeningDecorator这样一个静态方法，
     * 入参为juc包的线程池对象，这里采用了委托设计模式。
     * 当提交一个任务后，返回一个ListenableFuture对象，这个对象可以被Futures.addCallback作为一个入参，作为被监听的任务，
     * 第二个入参为监听的回调函数，需要实现成功的回调和异常捕获后的回调，
     * 第三个为线程池对象，当回调被触发时，由它执行回调函数任务。
     * 有点类似于生产者与消费者，第一个线程池负责任务的生产，第二个线程池负责任务结果的消费
     * */


    private static  ListeningExecutorService gpool = null;

    static {
        ThreadPoolExecutor pool = ThreadUtil.getCpuIntenseTargetThreadPool();

        gpool = MoreExecutors.listeningDecorator(pool);
    }


    public CallbackTaskScheduler() {
    }


    /**
     * 添加任务
     * */
    public static <R> void add(CallbackTask<R> callbackTask) {
        ListenableFuture<R> future= gpool.submit(()->{
            R r = null;
            try {
                 r = callbackTask.execute();


            } catch (Exception e) {
                e.printStackTrace();
            }
            return r;

        });

        Futures.addCallback(future, new FutureCallback<R>() {
            @Override
            public void onSuccess(R r) { //成功的回调
                callbackTask.onBack(r);
            }

            @Override
            public void onFailure(Throwable throwable) { //异步回调
                callbackTask.onException(throwable);
            }
        });
    }
}
