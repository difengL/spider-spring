package com.self.spider;

import com.alibaba.fastjson.JSONObject;
import com.self.spider.entities.TitleDetail;
import com.self.spider.entities.dto.QueryAvCondition;
import com.self.spider.scheduled.thread.BT.LoadMarkSourceTask;
import com.self.spider.servies.remote.AvMapper;
import lombok.val;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Consumer;

/**
 * Author:  liuyao20
 * Date: Created in 2022/1/18 19:41
 * Description：
 */
@SpringBootTest
public class MarkLable {

    @Resource
    private AvMapper mapper;

    @Resource
    LoadMarkSourceTask loadMarkSourceTask;

    @Test
    void test(){
        for (int i = 0; i < 54; i++) {
            QueryAvCondition condition = QueryAvCondition.builder()
                    .tableName("av_list")
                    .starNum(i*100)
                    .pageSize(100)
                    .build();
            System.out.println(i*100 +" ~ " +(i+1)*100);
            val titleDetails = mapper.queryAll(condition);
            titleDetails.forEach(item -> {
                MAKE_MARK.accept(item);
                mapper.updateType(item.getId(),item.getTypes());
            });
        }

    }




    @Test
    void test1(){
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            TitleDetail detail = TitleDetail.builder()
                    //25个字
                    .name("[12.16] [MP4/ 1.16G] GS-393 惩罚女员工！全裸只穿连裤袜羞耻攻击！ 只穿一条连裤袜！[中文字幕]")
                    .build();
            MAKE_MARK.accept(detail);
            System.out.println(JSONObject.toJSONString(detail));
        }
        System.out.println(System.currentTimeMillis() - t1);
    }





    /**
     * 打标签
     */
    private final Consumer<TitleDetail> MAKE_MARK = detail ->{

        val markSource = LoadMarkSourceTask.MARK_SOURCE;
        if(LoadMarkSourceTask.MARK_SOURCE.size() == 0 ){
            long t1 =  System.currentTimeMillis();
            loadMarkSourceTask.configureTasks();
            System.out.println("扣除数据库链接时间："+(System.currentTimeMillis()-t1));
        }


        boolean action = MapUtils.isEmpty(markSource) || Objects.isNull(detail) || StringUtils.isBlank(detail.getName());
        if (action) {
            return;
        }
        final Map<String,String> mark = new HashMap<>();
        final String title = detail.getName();
        final int totleSize = title.length();
        int star = 0,step = 1;
        for (;;){
            final String str = title.substring(star,star + step);
            String nowMark = markSource.get(str);
            boolean action1 = StringUtils.isNotBlank(nowMark);
            if(action1){
                Arrays.stream(nowMark.split(",")).forEach(item -> mark.put(item,null));
            }
            //处理完之后调整下标
            star = star + 1;
            if(star + step > totleSize){
                star = 0;
                step = step + 1;
            }
            //如果step = totleSize则退出
            if(step > totleSize || step > LoadMarkSourceTask.MAX_LENGTH.get()){
                break;
            }
        }
        //循环完成之后，取出所有打的标签
        detail.setTypes(String.join(",",new ArrayList<>(mark.keySet())));
    };



    /**
     * 打标签
     */
    private final Consumer<TitleDetail> MAKE_MARK_COPY = detail ->{
        val markSource = LoadMarkSourceTask.MARK_SOURCE;
        if(LoadMarkSourceTask.MARK_SOURCE.size() == 0 ){
            long t1 =  System.currentTimeMillis();
            loadMarkSourceTask.configureTasks();
            System.out.println("扣除数据库链接时间："+(System.currentTimeMillis()-t1));
        }

        boolean action = MapUtils.isEmpty(markSource) || Objects.isNull(detail) || StringUtils.isBlank(detail.getName());
        if (action) {
            return;
        }
        final Map<String,String> mark = new HashMap<>();
        final String title = detail.getName();

        markSource.forEach((k,v) -> {
            if (title.contains(k)){
                Arrays.stream(v.split(",")).forEach(item -> mark.put(item,null));
            }
        });
        //循环完成之后，取出所有打的标签
        detail.setTypes(String.join(",",new ArrayList<>(mark.keySet())));
    };

}
