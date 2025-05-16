package com.weiyin.qinplus.ui.tv.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PublicWorker {
    private ExecutorService executor = Executors.newFixedThreadPool(10);

    private ExecutorService instantExecutor = Executors.newCachedThreadPool();

    // 执行任务, 放入队列, 不需要立即执行
    public void pushTask(Runnable r) {
        executor.submit(r);
    }

    // 执行需要立即执行的任务
    public void pushInstantTask(Runnable r) {
        instantExecutor.submit(r);
    }

    // 关闭队列
    public void shutdown() {
        executor.shutdownNow();
    }
}
