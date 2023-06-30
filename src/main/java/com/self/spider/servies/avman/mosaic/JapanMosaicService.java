package com.self.spider.servies.avman.mosaic;

import com.self.spider.entities.TitleDetail;
import com.self.spider.servies.avman.AbstractAvmanService;
import com.self.spider.servies.remote.AvMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class JapanMosaicService extends AbstractAvmanService {


    @Resource
    private AvMapper mapper;

    @Override
    protected String getTableUrl() {
        return "https://apa11.xyz/Category/%E6%9C%89%E7%A2%BC%E5%BD%B1%E7%89%87";
    }

    @Override
    protected void saveData(TitleDetail detail) throws Exception {
        detail.setTableName("av_list");
        detail.setAudit(0);
        detail.setMosaic("有");
        mapper.addInfo(detail);
    }
}
