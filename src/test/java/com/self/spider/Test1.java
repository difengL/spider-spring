package com.self.spider;

import com.alibaba.fastjson.JSONObject;
import com.self.spider.child.SyncTest;
import com.self.spider.entities.TitleDetail;
import com.self.spider.entities.business.Catalogue;
import com.self.spider.scheduled.thread.BT.LoadMarkSourceTask;
import com.self.spider.servies.c5cbca7s.manager.CinfigManager;
import com.self.spider.toolkits.GetToolKit;
import com.self.spider.toolkits.PropertiesKits;
import lombok.val;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class Test1 {

    public static void main(String[] args) throws InterruptedException, IOException {
      String url = CinfigManager.getInstons().getPrefix()+ "thread.php?fid=110";
        System.out.println(url);
        String result = GetToolKit.get_https(url);
        System.out.println(result);

    }

    /**
     * 打标签
     */
    private static final Consumer<TitleDetail> MAKE_MARK = detail ->{
        val markSource = LoadMarkSourceTask.MARK_SOURCE;
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
}
