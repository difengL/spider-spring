package com.self.spider.scheduled.thread.BT;

import java.util.concurrent.*;

/**
 * Author:  liuyao20
 * Date: Created in 2021/12/28 15:27
 * Description：
 */
public class WorkerThreadPool {

    public static final ThreadPoolExecutor THREADPOOL = new ThreadPoolExecutor(1, 1, 0,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
            new ThreadPoolExecutor.DiscardOldestPolicy());
}
