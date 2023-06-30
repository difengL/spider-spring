package com.self.spider.man;

import com.alibaba.fastjson.JSONObject;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import com.self.spider.entities.TitleDetail;
import com.self.spider.toolkits.GetToolKit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.platform.commons.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Set;
import java.util.stream.Collectors;

public class Test2 {

    public static void main(String[] args) throws UnsupportedEncodingException {
        String url = "https://apa11.xyz/Category/%E6%9C%89%E7%A2%BC%E5%BD%B1%E7%89%87/1";

        String result = GetToolKit.get_https(url);
        Document document = Jsoup.parse(result);
        Elements pageList = document.getElementsByClass("pt-1").select(".col");
        pageList.stream().forEach(item -> {
            String imgUrl = item.getElementsByTag("img").attr("src");
            Elements aLib = item.getElementsByTag("a");
            String detailUrl = aLib.attr("href");
            String title = aLib.attr("title");
            //String pubTime = item.getElementsByTag("p").text();
            if(StringUtils.isNotBlank(title) && !title.contains("特報") && !title.contains("合集") && !title.contains("專輯")){
                TitleDetail detail = TitleDetail.builder()
                        .name(title)
                        .img(imgUrl)
                        .mosaic("是")
                        .build();
                detailContent(detailUrl,detail);
            }
        });
    }


    public static void detailContent(String detailUrl, TitleDetail detail){
        String result = GetToolKit.get_https(detailUrl);
        //System.out.println(result);
        Document document = Jsoup.parse(result);
        String tag = document.getElementsByClass("single_show_lb")
                .select("p")
                .select("a").text();
        String md5 = document.getElementsByClass("text-justify").eq(0).select("a").eq(0).attr("href");
        detail.setMd5(md5);
        detail.setTypes(ZhConverterUtil.convertToSimple(tag));
        //获取actor  先从标题
        String actor = document.getElementsByClass("single_show_yy").select("p").text();
        if(StringUtils.isBlank(actor)){
            String[] arr = detail.getName().split(" ");
            actor = arr[arr.length-1];
        }
        detail.setActor(actor);
        System.out.println(JSONObject.toJSONString(detail));
    }

}
