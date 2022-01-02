package com.self.spider.servies.c5cbca7s.japan;

import com.alibaba.fastjson.JSONObject;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import com.self.spider.entities.TitleDetail;
import com.self.spider.entities.business.Catalogue;
import com.self.spider.handler.c5cbca7s;
import com.self.spider.servies.c5cbca7s.AbstractC5cbca7sService;
import com.self.spider.servies.c5cbca7s.manager.CinfigManager;
import com.self.spider.servies.remote.AvMapper;
import com.self.spider.toolkits.GetToolKit;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BTjapanService extends AbstractC5cbca7sService {


    @Resource
    private AvMapper mapper;

    private static volatile int failCount = 0;

    public boolean detail(List<Catalogue> detailUrl){

        detailUrl.forEach(element -> {
            String result;
            Document document;
            int forCount = 5;
            for (;;){
                result = GetToolKit.get_https(element.getUrl());
                document = Jsoup.parse(result);
                boolean action = StringUtils.isNotBlank(result) && Objects.nonNull(document);
                forCount --;
                if(action||forCount<=0){
                    break;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                }
            }
            //影片名称
            Map<String,String> map = new HashMap<>();
            String [] arr = document.getElementById("read_tpc").html().split("\n");
            Arrays.stream(arr)
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
                    .name(ZhConverterUtil.convertToSimple(element.getTitle().trim()))
                    .actor(ZhConverterUtil.convertToSimple(actor.trim()))
                    .dowonUrl(c5cbca7s.DOWONLOAD_URL.apply(map).trim())
                    .mosaic(c5cbca7s.MOSAIC.apply(map).trim())
                    .img(c5cbca7s.IMG.apply(map).trim())
                    .build();

            //System.out.println(JSONObject.toJSONString(detail));
            try {
                mapper.addInfo(detail);
                failCount = 0;
            }catch (Exception e){
                failCount ++;
                System.out.println("【保存失败】"+JSONObject.toJSONString(detail));
            }

        });
        if(failCount>=10){
            return Boolean.FALSE;
        }
        failCount = 0;
        return Boolean.TRUE;
    }

}
