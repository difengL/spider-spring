package com.self.spider.scheduled.thread.BT;

import java.util.concurrent.*;

/**
 * Author:  liuyao20
 * Date: Created in 2021/12/28 15:27
 * Description：
 */
public class WorkerThreadPool {

    public static final ThreadPoolExecutor THREADPOOL = new ThreadPoolExecutor(5, 8, 100,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
            new ThreadPoolExecutor.DiscardOldestPolicy());
}
