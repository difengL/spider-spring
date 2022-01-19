package com.self.spider.scheduled.thread.BT;

import com.self.spider.entities.AutoMark;
import com.self.spider.servies.remote.AutoMarkMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author:  liuyao20
 * Date: Created in 2022/1/18 17:50
 * Description：
 */

@Component
@Configuration
@EnableScheduling
public class LoadMarkSourceTask {

    public final static Map<String,String> MARK_SOURCE = new HashMap<>();

    public final static AtomicInteger MAX_LENGTH = new AtomicInteger(0);

    @Resource
    private AutoMarkMapper autoMarkMapper;


    @Scheduled(cron = "0 0 */1 * * ?")
    public void configureTasks() {
        //查询所有标签
        MARK_SOURCE.clear();
        List<AutoMark> autoMarkList = autoMarkMapper.queryAllMark();
        if(CollectionUtils.isNotEmpty(autoMarkList)){
            autoMarkList.forEach(item -> {
                MARK_SOURCE.put(item.getKeyWord(),item.getMarkType());
                if(item.getKeyWord().length() > MAX_LENGTH.get()){
                    MAX_LENGTH.set(item.getKeyWord().length());
                }
            });
        }
    }


}
