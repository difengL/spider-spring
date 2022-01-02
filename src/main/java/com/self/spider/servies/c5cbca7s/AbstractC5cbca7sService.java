package com.self.spider.servies.c5cbca7s;


import com.self.spider.entities.business.Catalogue;
import com.self.spider.servies.c5cbca7s.manager.CinfigManager;
import com.self.spider.toolkits.GetToolKit;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.stream.Collectors;

public class AbstractC5cbca7sService {

    public List<Catalogue> titleList(String url){

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
                .map(catalogue ->  Catalogue.builder().title(catalogue.getTitle()).url(CinfigManager.getInstons().getPrefix()+ catalogue.getUrl()).build())
                .collect(Collectors.toList());
        return titleUrl;
    }



}
