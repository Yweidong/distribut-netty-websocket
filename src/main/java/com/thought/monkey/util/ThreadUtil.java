package com.thought.monkey.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Auther: wei_tung
 * @Date: 2021/4/29 15:46
 * @Description: 1371690483@qq.com
 */
public class ThreadUtil {
    /**
     * CPU核数
     **/
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * 空闲保活时限，单位秒
     */
    private static final int KEEP_ALIVE_SECONDS = 30;

    /**
     * 有界队列size
     */
    private static final int QUEUE_SIZE = 10000;

    /**
     * 核心线程数
     */
    private static final int CORE_POOL_SIZE = 0;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT;

    /**
     * 获取执行CPU密集型任务的线程池
     *
     * @return
     */
    public static ThreadPoolExecutor getCpuIntenseTargetThreadPool() {
        return CpuIntenseTargetThreadPoolLazyHolder.EXECUTOR;
    }

    private static class CpuIntenseTargetThreadPoolLazyHolder {
        //线程池： 用于CPU密集型任务
        private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
                MAXIMUM_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue(QUEUE_SIZE),
                new CustomThreadFactory("cpu")
        );
        static {
            EXECUTOR.allowCoreThreadTimeOut(true);
            //JVM关闭时的钩子函数
            Runtime.getRuntime().addShutdownHook(
                    new ShutdownHookThread("CPU密集型任务线程池", new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            //优雅关闭线程池
                            shutdownThreadPoolGracefully(EXECUTOR);
                            return null;
                        }
                    }));
        }

    }

    public static void shutdownThreadPoolGracefully(ExecutorService threadPool) {
        if (!(threadPool instanceof ExecutorService) || threadPool.isTerminated()) {
            return;
        }
        try {
            threadPool.shutdown();   //拒绝接受新任务
        } catch (SecurityException e) {
            return;
        } catch (NullPointerException e) {
            return;
        }
        try {
            // 等待 60 s，等待线程池中的任务完成执行
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                // 调用 shutdownNow 取消正在执行的任务
                threadPool.shutdownNow();
                // 再次等待 60 s，如果还未结束，可以再次尝试，或则直接放弃
                if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("线程池任务未正常执行结束");
                }
            }
        } catch (InterruptedException ie) {
            // 捕获异常，重新调用 shutdownNow
            threadPool.shutdownNow();
        }
        //任然没有关闭，循环关闭1000次，每次等待10毫秒
        if (!threadPool.isTerminated()) {
            try {
                for (int i = 0; i < 1000; i++) {
                    if (threadPool.awaitTermination(10, TimeUnit.MILLISECONDS)) {
                        break;
                    }
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            } catch (Throwable e) {
                System.err.println(e.getMessage());
            }
        }
    }

    static class ShutdownHookThread extends Thread {
        private volatile boolean hasShutdown = false;
        private static AtomicInteger shutdownTimes = new AtomicInteger(0);
        private final Callable callback;

        /**
         * Create the standard hook thread, with a call back, by using {@link Callable} interface.
         *
         * @param name
         * @param callback The call back function.
         */
        public ShutdownHookThread(String name, Callable callback) {
            super("JVM退出钩子(" + name + ")");

            this.callback = callback;
        }

        /**
         * Thread run method.
         * Invoke when the jvm shutdown.
         */
        @Override
        public void run() {
            synchronized (this) {
                System.out.println(getName() + " starting.... ");
                if (!this.hasShutdown) {
                    this.hasShutdown = true;
                    long beginTime = System.currentTimeMillis();
                    try {
                        this.callback.call();
                    } catch (Exception e) {
                        System.out.println(getName() + " error: " + e.getMessage());
                    }
                    long consumingTimeTotal = System.currentTimeMillis() - beginTime;
                    System.out.println(getName() + "  耗时(ms): " + consumingTimeTotal);
                }
            }
        }
    }


    /**
     * 定制线程的工厂
     * */
    private static class CustomThreadFactory implements ThreadFactory {


        private static final AtomicInteger poolNumber = new AtomicInteger(1);//原子类，线程池编号

        private final ThreadGroup group;//线程组

        private final AtomicInteger threadNumber = new AtomicInteger(1);//线程数目
        private final String namePrefix;//为每个创建的线程添加的前缀

        public CustomThreadFactory(String namePrefix) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();//取得线程组
            this.namePrefix = "pool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);//真正创建线程的地方，设置了线程的线程组及线程名
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)//默认是正常优先级
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
