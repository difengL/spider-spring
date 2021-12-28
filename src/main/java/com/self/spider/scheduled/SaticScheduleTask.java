package com.self.spider.scheduled;

import com.alibaba.fastjson.JSONObject;
import com.self.spider.entities.TitleDetail;
import com.self.spider.entities.business.Catalogue;
import com.self.spider.handler.c5cbca7s;
import com.self.spider.servies.remote.AvMapper;
import com.self.spider.toolkits.GetToolKit;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class SaticScheduleTask {

    private static final String PREFIX = "http://q2.2112ks.link/pw/";

    @Resource
    private AvMapper mapper;

    private static volatile int failCount = 0;

    //3.添加定时任务
    @Scheduled(cron = "0 25 5 * * ?")
    //或直接指定时间间隔，例如：5秒
    //@Scheduled(fixedRate=5000)
    private void configureTasks() {
        for (int i = 1; i < 10 && failCount <10; i++) {
            System.out.println("当前第【"+i+"】页");
            String url = "http://q2.2112ks.link/pw/thread.php?fid=22&page="+i;
            List<Catalogue> detailUrl = null;

            detailUrl = titleList(url);
            detail(detailUrl);
        }

    }



    private void detail(List<Catalogue> detailUrl){

        detailUrl.forEach(element -> {
            String result = GetToolKit.get_https(element.getUrl());
            Document document = Jsoup.parse(result);
            //影片名称
            Map<String,String> map = new HashMap<>();
            String [] arr = document.getElementById("read_tpc").html().split("\n");
            Arrays.stream(arr)
                    //.filter(item -> item.contains("【") && item.contains("】") || item.contains("<img "))
                    .map(item -> item.replace("<br>",""))
                    .map(item -> item.replace("&nbsp;"," "))
                    .forEach(item -> {
                        //System.out.println(item);
                        String [] kv = item.split("：");
                        if(kv.length==1){
                            map.put(kv[0],"");
                        }else if(kv.length==2){
                            map.put(kv[0],kv[1]);
                        }
                    });
            String actor = c5cbca7s.ACTOR_NAME.apply(map);
            //首先从标题里面取值
            if(element.getTitle().contains("VIP")){
                actor = element.getTitle().substring(0,element.getTitle().indexOf("VIP"));
                actor = actor.substring(actor.trim().lastIndexOf(" "));
                actor = StringUtils.deleteWhitespace(actor);
            }else if("人工识别".equals(actor)){
                actor = element.getTitle().substring(element.getTitle().trim().lastIndexOf(" ")+1);
            }
            actor = actor.trim().replace("[中文字幕]","");
            if(actor.endsWith("[")||actor.endsWith("【")) {
                actor = actor.substring(0,actor.length()-1);
            }
            TitleDetail detail = TitleDetail.builder()
                    .name(element.getTitle().trim())
                    .actor(actor.trim())
                    .dowonUrl(c5cbca7s.DOWONLOAD_URL.apply(map).trim())
                    .mosaic(c5cbca7s.MOSAIC.apply(map).trim())
                    .img(c5cbca7s.IMG.apply(map).trim())
                    .build();

            System.out.println(JSONObject.toJSONString(detail));
            try {
                TitleDetail info = mapper.queryByUrl(detail.getDowonUrl());
               /* if(info!=null){
                    mapper.updateInfo(detail);
                }else{
                    mapper.addInfo(detail);
                }*/
                mapper.addInfo(detail);
                failCount = 0;
            }catch (Exception e){
                failCount ++;
                System.out.println("【保存失败】"+JSONObject.toJSONString(detail));
            }


        });
    }



    public static List<Catalogue> titleList(String url){

        List<Catalogue> titleUrl;
        String result = GetToolKit.get_https(url);
        Document document = Jsoup.parse(result);
        Elements trList = document.getElementById("ajaxtable").getElementsByTag("tr");
        titleUrl = trList.stream()
                .map(element -> {
                    Elements ele = element.select("td").eq(1).select("a").eq(1);
                    return Catalogue.builder()
                            .title(ele.text())
                            .url(ele.attr("href"))
                            .build();
                })
                .filter(catalogue -> StringUtils.isNotBlank(catalogue.getTitle())&&StringUtils.isNotBlank(catalogue.getUrl()))
                .filter(catalogue -> catalogue.getUrl().contains("html_data"))
                .map(catalogue ->  Catalogue.builder().title(catalogue.getTitle()).url(PREFIX + catalogue.getUrl()).build())
                .collect(Collectors.toList());
        return titleUrl;
    }
}