package com.self.spider.servies.c5cbca7s.japan;

import com.self.spider.entities.TitleDetail;
import com.self.spider.scheduled.thread.BT.LoadMarkSourceTask;
import com.self.spider.servies.c5cbca7s.AbstractC5cbca7sService;
import com.self.spider.servies.c5cbca7s.manager.CinfigManager;
import com.self.spider.servies.remote.AvMapper;
import lombok.val;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Consumer;

@Service
public class BTjapanService extends AbstractC5cbca7sService {

    @Resource
    private AvMapper mapper;

    @Resource
    LoadMarkSourceTask loadMarkSourceTask;

    @Override
    public String getTableUrl(){
        return CinfigManager.getInstons().getPrefix() + "thread.php?fid=22";
    }

    @Override
    protected void saveData(TitleDetail detail){

        MAKE_MARK.accept(detail);
        detail.setTableName("av_list");
        detail.setAudit(0);
        mapper.addInfo(detail);
    }

    /**
     * 打标签
     */
    private final Consumer<TitleDetail> MAKE_MARK = detail ->{
        val markSource = LoadMarkSourceTask.MARK_SOURCE;
        if(LoadMarkSourceTask.MARK_SOURCE.size() == 0 ){
            loadMarkSourceTask.configureTasks();
        }

        boolean action = MapUtils.isEmpty(markSource) || Objects.isNull(detail) || StringUtils.isBlank(detail.getName());
        if (action) {
            return;
        }
        final Map<String,String> mark = new HashMap<>();
        final String title = detail.getName();
        final int totleSize = title.length();
        int star = 0,step = 1;
        for (;;){
            final String str = title.substring(star,star + step);
            String nowMark = markSource.get(str);
            boolean action1 = StringUtils.isNotBlank(nowMark);
            if(action1){
                Arrays.stream(nowMark.split(",")).forEach(item -> mark.put(item,null));
            }
            //处理完之后调整下标
            star = star + 1;
            if(star + step > totleSize){
                star = 0;
                step = step + 1;
            }
            //如果step = totleSize则退出
            if(step > totleSize || step > LoadMarkSourceTask.MAX_LENGTH.get()){
                break;
            }
        }
        //循环完成之后，取出所有打的标签
        detail.setTypes(String.join(",",new ArrayList<>(mark.keySet())));
    };

}
