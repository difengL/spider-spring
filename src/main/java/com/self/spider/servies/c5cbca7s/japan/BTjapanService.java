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



    public void spiderData(int startNum,int num){
        for (int i = startNum; i < startNum+num ; i++) {
            String url = CinfigManager.getInstons().getPrefix() + "thread.php?fid=22&page="+i;
            List<Catalogue> detailUrl = titleList(url);
            if(!detail(detailUrl)){
                return;
            }
        }
    }


    @Override
    protected void saveData(TitleDetail detail) throws Exception {
        detail.setTableName("av_list");
        mapper.addInfo(detail);
    }
}
