package com.self.spider.handler;


import com.alibaba.fastjson.JSONObject;
import com.self.spider.constant.TestDataSource;
import com.self.spider.entities.TitleDetail;
import com.self.spider.toolkits.GetToolKit;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;


public class c5cbca7s {

    private static final String PREFIX = "https://k6.c5cbca7s.com/pw/";


    public static void main(String[] args) {

        String url = "https://k6.c5cbca7s.com/pw/thread.php?fid=22&page=1";
        List<String> detailUrl = null;

        detailUrl = TestDataSource.getData();
        //detailUrl = titleList(url);
        detailUrl.forEach(element -> {
            String result = GetToolKit.get_https(element);
            Document document = Jsoup.parse(result);


            //影片名称
            AtomicReference<String> name = new AtomicReference<>("");
            Map<String,String> map = new HashMap<>();
            String [] arr = document.getElementById("read_tpc").html().split("\n");
            Arrays.stream(arr)
                    //.filter(item -> item.contains("【") && item.contains("】") || item.contains("<img "))
                    .map(item -> item.replace("<br>",""))
                    .map(item -> item.replace("&nbsp;"," "))
                    .forEach(item -> {
                        boolean action = StringUtils.isNotBlank(StringUtils.deleteWhitespace(item));
                        action = action && StringUtils.isBlank(name.get());
                        if(action){
                            name.set(item);
                        }
                        //System.out.println(item);
                        String [] kv = item.split("：");
                        if(kv.length==1){
                            map.put(kv[0],"");
                        }else if(kv.length==2){
                            map.put(kv[0],kv[1]);
                        }
                    });
            TitleDetail detail = TitleDetail.builder()
                    .name(name.get())
                    .actor(ACTOR_NAME.apply(map))
                    .dowonUrl(DOWONLOAD_URL.apply(map))
                    .mosaic(MOSAIC.apply(map))
                    .img(IMG.apply(map))
                    .build();

            System.out.println(JSONObject.toJSONString(detail));


        });


    }

    public static List<String> titleList(String url){
        List<String> titleUrl;
        String result = GetToolKit.get_https(url);
        Document document = Jsoup.parse(result);
        Elements trList = document.getElementById("ajaxtable").getElementsByTag("tr");
        titleUrl = trList.stream()
                .map(element -> element.select("td").eq(1).select("a").eq(1).attr("href"))
                .filter(StringUtils::isNotBlank)
                .filter(element -> element.startsWith("html_data"))
                .map(element -> PREFIX + element)
                .collect(Collectors.toList());
        return titleUrl;
    }

    public static Function<Map<String,String>,String>  ACTOR_NAME = map ->  {
        final String defutleName = "人工识别";
        String actor = map.getOrDefault("AV女優",defutleName);
        /*if(StringUtils.isBlank(actor = StringUtils.deleteWhitespace(actor)) || defutleName.equals(actor)){
            actor = StringUtils.deleteWhitespace(map.get("【影片名称代号】"));
            if(actor.contains(" ")){
                actor = actor.substring(actor.lastIndexOf(" ")+1);
            }
        }*/
        return actor;
    };

    public static Function<Map<String,String>,String>  DOWONLOAD_URL = map ->  {
        AtomicReference<String> url = new AtomicReference<>("找不到下载地址");
        map.forEach((k,v) ->{
            if(k.contains("/torrent/")){
                url.set(Jsoup.parse(k).getElementsByTag("a").attr("href"));
            }
        });
        return url.get();
    };

    public static Function<Map<String,String>,String>  MOSAIC = map ->  {
        String url = map.getOrDefault("【是否有码】","未知");
        url = url.replace("：","");
        return url;
    };


    public static Function<Map<String,String>,String>  IMG = map ->  {
        AtomicReference<String> imgs = new AtomicReference<>("找不到图片");
        map.forEach((k,v) ->{
            if(k.contains(".jp")){
                imgs.set(imgs.get()+","+Jsoup.parse(k).getElementsByTag("img").attr("src"));
            }
        });
        return imgs.get().replace("找不到图片,","");
    };






}
