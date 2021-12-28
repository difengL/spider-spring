package com.self.spider.servies.c5cbca7s;


import com.self.spider.entities.business.Catalogue;
import com.self.spider.toolkits.GetToolKit;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.stream.Collectors;

public class AbstractC5cbca7sService {

    private final String PREFIX = "https://k6.c5cbca7s.com/pw/";



    //获取列表页
    public List<Catalogue> titleList(String url){
        int a = 1;
        if(url.contains("fid=110")){
            a = 0;
        }


        List<Catalogue> titleUrl;
        String result = GetToolKit.get_https(url);
        Document document = Jsoup.parse(result);
        Elements trList = document.getElementById("ajaxtable").getElementsByTag("tr");
        int finalA = a;
        titleUrl = trList.stream()
                .map(element -> {
                    Elements ele = element.select("td").eq(1).select("a").eq(finalA);
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
