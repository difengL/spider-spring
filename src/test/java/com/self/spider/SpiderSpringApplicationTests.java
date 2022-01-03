package com.self.spider;

import com.alibaba.fastjson.JSONObject;
import com.self.spider.constant.TestDataSource;
import com.self.spider.entities.TitleDetail;
import com.self.spider.entities.business.Catalogue;
import com.self.spider.handler.c5cbca7s;
import com.self.spider.servies.c5cbca7s.china.BTchinaService;
import com.self.spider.servies.c5cbca7s.japan.BTjapanService;
import com.self.spider.servies.remote.AvMapper;
import com.self.spider.toolkits.GetToolKit;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@SpringBootTest
class SpiderSpringApplicationTests {

    private static final String PREFIX = "http://k7.2112kw.com/pw/";

    @Resource
    private AvMapper mapper;


    @Resource
    private BTjapanService japanService;

    @Resource
    private BTchinaService chinaService;

    @Test
    void contextLoads() {

        japanService.spiderData(3,2);

       /* TitleDetail info = mapper.queryByUrl("https://ww1.bi22t.club/torrent/393615F6C515D78A492E31731E2104F26CEBFBE1");
        System.out.println(JSONObject.toJSONString(info));*/

      /* for (int i = 40; i < 56; i++) {
            System.out.println("当前第【"+i+"】页");

            String url = "http://k7.2112ky.com/pw/thread.php?fid=110&page="+i;
            List<Catalogue> detailUrl = null;

           detailUrl= titleList(url);
           *//* titleList(url).stream().forEach(item -> {
                System.out.println(JSONObject.toJSONString(item));
            });*//*

            detail(detailUrl);
        }*/

        /*titleList("http://k6.c5cbca7s.com/pw/thread.php?fid=110&page=1").stream().forEach(item -> {

            System.out.println(JSONObject.toJSONString(item));
        });*/
      /*  List<Catalogue> detailUrl = new ArrayList<>();
        detailUrl.add(Catalogue.builder().url("http://q2.2112ks.link/pw/html_data/22/2112/5752866.html").title(" [FHD/MP4]SDJS131 SOD女子社員").build());
        detail(detailUrl);*/



    }


    private void detail(List<Catalogue> detailUrl){

        detailUrl.forEach(element -> {

            try {
                String result = GetToolKit.get_https(element.getUrl());
                Document document = Jsoup.parse(result);
                if(null==document){
                    return;
                }

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
                if(actor.endsWith("[")||actor.endsWith("【"))actor = actor.substring(0,actor.length()-1);
                TitleDetail detail = TitleDetail.builder()
                        .name(element.getTitle().trim())
                        .actor("")
                        .dowonUrl(c5cbca7s.DOWONLOAD_URL.apply(map).trim())
                        .mosaic(c5cbca7s.MOSAIC.apply(map).trim())
                        .img(c5cbca7s.IMG.apply(map).trim())
                        .tableName("av_list_china")
                        .build();

                System.out.println(JSONObject.toJSONString(detail));
                mapper.addInfo(detail);
                Thread.sleep(500);
                //TitleDetail info = mapper.queryByUrl(detail.getDowonUrl());
               /* if(info!=null){
                    mapper.updateInfo(detail);
                }else{
                    mapper.addInfo(detail);
                }*/
                //mapper.addInfo(detail);

            }catch (Exception e){
                System.out.println("【保存失败】"+JSONObject.toJSONString(detailUrl));
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
                .map(catalogue ->  Catalogue.builder().title(catalogue.getTitle()).url(PREFIX + catalogue.getUrl()).build())
                .collect(Collectors.toList());
        return titleUrl;
    }

}
