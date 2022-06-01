package com.self.spider.servies.c5cbca7s;


import com.alibaba.fastjson.JSONObject;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import com.self.spider.constant.SpiderConfig;
import com.self.spider.entities.TitleDetail;
import com.self.spider.entities.business.Catalogue;
import com.self.spider.handler.c5cbca7s;
import com.self.spider.servies.c5cbca7s.manager.CinfigManager;
import com.self.spider.toolkits.GetToolKit;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public abstract class AbstractC5cbca7sService {


    /**
     * 循环目录页
     */
    public void spiderData(int startNum,int num){
        for (int i = startNum; i < startNum+num ; i++) {
            System.out.printf("第【%s】页%n",i);
            String url = getTableUrl() + "&page="+i;
            List<Catalogue> detailUrl = titleList(url);
            if(!detail(detailUrl)){
                System.out.printf("连续保存失败达到【%s】次，终止爬虫。%n",SpiderConfig.FAIL_LIMIT_COUNT);
                break;
            }
        }
    }

    protected abstract String getTableUrl();

    /**
     * 根据url获取目录页列表
     */
    protected List<Catalogue> titleList(String url){

        List<Catalogue> titleUrl;
        String result = GetToolKit.get_https(url);
        Document document = Jsoup.parse(result);
        Elements trList = document.getElementById("ajaxtable").getElementsByTag("tr");
        if(trList == null || trList.size()<=0){
            url = recentlyUrl(url);
            result = GetToolKit.get_https(url);
            document = Jsoup.parse(result);
            trList = document.getElementById("ajaxtable").getElementsByTag("tr");
        }
        titleUrl = trList.stream()
                .map(element -> {
                    Elements ele = element.select("td").eq(1).select("a");
                    Elements e = ele.eq(0);
                    if(ele.size()>1){
                        e = ele.eq(1);
                    }
                    return Catalogue.builder()
                            .title(e.text())
                            .url(e.attr("href"))
                            .build();
                })
                .filter(catalogue -> StringUtils.isNotBlank(catalogue.getTitle())&&StringUtils.isNotBlank(catalogue.getUrl()))
                .filter(catalogue -> catalogue.getUrl().contains("html_data"))
                .map(catalogue ->  Catalogue.builder().title(catalogue.getTitle()).url(CinfigManager.getInstons().getPrefix()+ catalogue.getUrl()).build())
                .collect(Collectors.toList());
        return titleUrl;
    }




    protected boolean detail(List<Catalogue> detailUrl){
        //统计连续失败的次数
        AtomicInteger failCount = new AtomicInteger(0);
        for (Catalogue element : detailUrl) {

            try {
                String result;
                Document document;
                int forCount = 5;
                //如果一次发送https失败，则重试5次
                for (; ; ) {
                    result = GetToolKit.get_https(element.getUrl());
                    if(StringUtils.isBlank(result)){
                        forCount--;
                        continue;
                    }
                    document = Jsoup.parse(result);
                    boolean action = StringUtils.isNotBlank(result) && Objects.nonNull(document);
                    forCount--;
                    if (action || forCount <= 0) {
                        break;
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ignored) {
                    }
                }
                //影片名称
                Map<String, String> map = new HashMap<>();
                String[] arr = document.getElementById("read_tpc").html().split("\n");
                Arrays.stream(arr)
                        .map(item -> item.replace("<br>", ""))
                        .map(item -> item.replace("&nbsp;", " "))
                        .forEach(item -> {
                            //System.out.println(item);
                            String[] kv = item.split("：");
                            if (kv.length == 1) {
                                map.put(kv[0], "");
                            } else if (kv.length == 2) {
                                map.put(kv[0], kv[1]);
                            }
                        });
                String actor = c5cbca7s.ACTOR_NAME.apply(map);
                //首先从标题里面取值
                if (element.getTitle().contains("VIP")) {
                    actor = element.getTitle().substring(0, element.getTitle().indexOf("VIP"));
                    actor = actor.substring(actor.trim().lastIndexOf(" "));
                    actor = StringUtils.deleteWhitespace(actor);
                } else if ("人工识别".equals(actor)) {
                    actor = element.getTitle().substring(element.getTitle().trim().lastIndexOf(" ") + 1);
                }
                actor = actor.trim().replace("[中文字幕]", "");
                if (actor.endsWith("[") || actor.endsWith("【")) {
                    actor = actor.substring(0, actor.length() - 1);
                }
                TitleDetail detail = TitleDetail.builder()
                        .name(ZhConverterUtil.convertToSimple(element.getTitle().trim()))
                        .actor(ZhConverterUtil.convertToSimple(actor.trim()))
                        .dowonUrl(c5cbca7s.DOWONLOAD_URL.apply(map).trim())
                        .mosaic(c5cbca7s.MOSAIC.apply(map).trim())
                        .img(c5cbca7s.IMG.apply(map).trim())
                        .build();
                try {
                    System.out.println(JSONObject.toJSONString(detail));
                    saveData(detail);
                    failCount.set(0);
                } catch (Exception e) {
                    failCount.getAndIncrement();
                    System.out.println("【保存失败】" + JSONObject.toJSONString(detail));
                }
            }catch (Exception ignoed){}



        }
        if(failCount.get() >= SpiderConfig.FAIL_LIMIT_COUNT){
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    protected abstract void saveData(TitleDetail detail) throws Exception;


    protected String recentlyUrl (String oldUrl){
        String result = GetToolKit.get_https(oldUrl);
        Document document = Jsoup.parse(result);
        Element ele = document.getElementById("headbase");
        String baseurl = ele.attr("href");
        CinfigManager.getInstons().setPrefix(baseurl,true);
        //把最新的写入到本地文件
        return baseurl;
    }



}
