package com.self.spider.scheduled.thread.BT;

import com.self.spider.entities.AutoMark;
import com.self.spider.entities.AvType;
import com.self.spider.servies.remote.AutoMarkMapper;
import com.self.spider.servies.remote.TypeMapper;
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
    /**
     * 关键字对应的标签
     */
    public final static Map<String,String> MARK_SOURCE = new HashMap<>();

    /**
     * 所有标签集合
     */
    public final static Map<String,Integer> tagSource = new HashMap<>();


    public final static AtomicInteger MAX_LENGTH = new AtomicInteger(0);

    @Resource
    private AutoMarkMapper autoMarkMapper;
    @Resource
    private TypeMapper typeMapper;


    @Scheduled(cron = "0 0 */20 * * ?")
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


    /**
     * 加载所有标签，做标签的匹配
     */
    @Scheduled(cron = "0 0 */30 * * ?")
    public void markTasks() {
        //查询所有标签
        tagSource.clear();
        List<AvType> typeList = typeMapper.queryAllType();
        if(CollectionUtils.isNotEmpty(typeList)){
            typeList.forEach(item -> {
                tagSource.put(item.getTypeName(),item.getId());
            });
        }
    }


}
