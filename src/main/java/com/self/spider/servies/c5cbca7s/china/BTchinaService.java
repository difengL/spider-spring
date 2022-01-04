package com.self.spider.servies.c5cbca7s.china;

import com.self.spider.entities.TitleDetail;
import com.self.spider.entities.business.Catalogue;
import com.self.spider.servies.c5cbca7s.AbstractC5cbca7sService;
import com.self.spider.servies.c5cbca7s.manager.CinfigManager;
import com.self.spider.servies.remote.AvMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BTchinaService extends AbstractC5cbca7sService {
    @Resource
    private AvMapper mapper;

    @Override
    public String getTableUrl(){
        return CinfigManager.getInstons().getPrefix() + "thread.php?fid=110";
    }

    @Override
    protected void saveData(TitleDetail detail) throws Exception {
        detail.setActor("");
        detail.setTableName("av_list_china");
        mapper.addInfo(detail);
    }
}
