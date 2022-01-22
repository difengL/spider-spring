package com.self.spider.servies.c5cbca7s.limit;

import com.self.spider.entities.TitleDetail;
import com.self.spider.servies.c5cbca7s.AbstractC5cbca7sService;
import com.self.spider.servies.c5cbca7s.manager.CinfigManager;
import com.self.spider.servies.remote.AvMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LimitLevelService extends AbstractC5cbca7sService {
    @Resource
    private AvMapper mapper;

    @Override
    public String getTableUrl(){
        return CinfigManager.getInstons().getPrefix() + "thread.php?fid=18";
    }

    @Override
    protected void saveData(TitleDetail detail) {
        detail.setActor("");
        detail.setTableName("av_list_limit");
        mapper.addInfo(detail);
    }


}
