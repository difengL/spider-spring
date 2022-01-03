package com.self.spider.scheduled.thread.BT;

import com.self.spider.entities.business.Catalogue;
import com.self.spider.servies.c5cbca7s.china.BTchinaService;
import com.self.spider.servies.c5cbca7s.japan.BTjapanService;
import com.self.spider.servies.c5cbca7s.manager.CinfigManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Configuration
@EnableScheduling
public class ChinaScheduleTask {

    @Resource
    private BTchinaService service;

    @Scheduled(cron = "0 30 5 * * ?")
    public void configureTasks() {
        int num = CinfigManager.getInstons().getNum();
        WorkerThreadPool.THREADPOOL.execute(() -> {
            service.spiderData(1,num);
        });
    }


}