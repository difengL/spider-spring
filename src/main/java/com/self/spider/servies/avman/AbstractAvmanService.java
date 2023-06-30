package com.self.spider.servies.avman;

import com.alibaba.fastjson.JSONObject;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import com.self.spider.constant.SpiderConfig;
import com.self.spider.entities.TitleDetail;
import com.self.spider.entities.business.Catalogue;
import com.self.spider.scheduled.thread.BT.LoadMarkSourceTask;
import com.self.spider.toolkits.GetToolKit;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractAvmanService {


    public void spiderData(int startNum,int num){
        for (int i = startNum; i < startNum+num ; i++) {
            System.out.printf("第【%s】页%n",i);
            String url = getTableUrl() + "/"+i;
            titleList(url);
        }
    }

    protected abstract String getTableUrl();



    protected void titleList(String url){
        String result = GetToolKit.get_https(url);
        Document document = Jsoup.parse(result);
        Elements pageList = document.getElementsByClass("pt-1").select(".col");
        pageList.stream().forEach(item -> {
            String imgUrl = item.getElementsByTag("img").attr("src");
            Elements aLib = item.getElementsByTag("a");
            String detailUrl = aLib.attr("href");
            String title = aLib.attr("title");
            if(StringUtils.isNotBlank(title) && !title.contains("特報") && !title.contains("合集") && !title.contains("專輯")){
                TitleDetail detail = TitleDetail.builder()
                        .name(title)
                        .img(imgUrl)
                        .build();
                detail(detailUrl,detail);
            }
        });
    }


    protected boolean detail(String detailUrl, TitleDetail detail){
        Map<String, Integer> tagSource = LoadMarkSourceTask.tagSource;
        String result = GetToolKit.get_https(detailUrl);
        Document document = Jsoup.parse(result);
        StringBuffer tag = new StringBuffer();
        document.getElementsByClass("single_show_lb")
        .select("p")
        .select("a")
        .stream().forEach(item -> {
            Integer mark = tagSource.get(ZhConverterUtil.convertToSimple(item.text()));
            if(Objects.nonNull(mark)){
                tag.append(mark).append(",");
            }

        });
        String md5 = document.getElementsByClass("text-justify").eq(0).select("a").eq(0).attr("href");
        detail.setMd5(md5);
        detail.setTypes(tag.substring(0,(tag.length() == 0 ? 1 : tag.length() )-1));
        //获取actor  先从标题
        String actor = document.getElementsByClass("single_show_yy").select("p").text();
        if(StringUtils.isBlank(actor)){
            String[] arr = detail.getName().split(" ");
            actor = arr[arr.length-1];
        }
        detail.setActor(actor);
        try {
            saveData(detail);
        }catch (Exception e){
            System.out.println("【保存失败】" + JSONObject.toJSONString(detail));
        }
        return true;
    }

    protected abstract void saveData(TitleDetail detail) throws Exception;

}
