package com.sczp.system.util.threadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolInstance {
    private ThreadPoolInstance(){}

    static class  StaticThreadPool{
        final static  ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
    }

    public static  ExecutorService getThreadPoolInstance(){
        return StaticThreadPool.fixedThreadPool;
    }

    public static  void threadPoolInstanceShutdown(){
        StaticThreadPool.fixedThreadPool.shutdown();
    }
}
