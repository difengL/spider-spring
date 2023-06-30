package com.self.spider.man;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import com.self.spider.toolkits.GetToolKit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Test1 {

    static Map<String,String> markType = new HashMap<>();

    static {
        markType.put("# Subject","主题");
        markType.put("# Role","角色");
        markType.put("# Clothing","制服");
        markType.put("# Body Type","身材");
        markType.put("# Behavior","行为");
        markType.put("# How to play","花样");
        markType.put("# Other","其他");
        markType.put("# Category","种类");
        markType.put("# Scene","场景");

    }










    static int num = 1;


    public static void main(String[] args) {
        String url = "https://apa11.xyz/Mark";
        String result = GetToolKit.get_https(url);
        Document document = Jsoup.parse(result);
        Elements allTag = document.getElementsByClass("tag_all");
        allTag.stream().forEach(tag -> {
            String subject = tag.select("h3").text();
            subject = markType.getOrDefault(subject,null);
            Elements aList = tag.select("a");
            String finalSubject = subject;
            aList.forEach(item -> {
                if(Objects.nonNull(finalSubject)){
                    StringBuffer sb = new StringBuffer("('");
                    sb.append(ZhConverterUtil.convertToSimple(item.text())).append("',");
                    sb.append(num).append(",'");
                    sb.append(finalSubject).append("'),");
                    System.out.println(sb);
                }

            });

            num = num + 10;

        });
        //System.out.println(result);
    }

}
