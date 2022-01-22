package com.self.spider.scheduled.thread.BT;

import com.self.spider.servies.c5cbca7s.limit.LimitLevelService;
import com.self.spider.servies.c5cbca7s.manager.CinfigManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Configuration
@EnableScheduling
public class LimitScheduleTask {

    @Resource
    private LimitLevelService service;

    @Scheduled(cron = "0 50 5 * * ?")
    public void configureTasks() {
        int num = CinfigManager.getInstons().getNum();
        int startNum = CinfigManager.getInstons().getStartNum();
        WorkerThreadPool.THREADPOOL.execute(() -> service.spiderData(startNum,num));
    }


}