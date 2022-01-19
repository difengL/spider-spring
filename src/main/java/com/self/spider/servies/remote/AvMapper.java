package com.self.spider.servies.remote;

import com.self.spider.entities.TitleDetail;
import com.self.spider.entities.dto.QueryAvCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AvMapper {

    int addInfo(TitleDetail detail);

    List<TitleDetail> queryAll(QueryAvCondition condition);

    int queryTotalPages(QueryAvCondition condition);

    List<String> queryAllActor();

    TitleDetail queryByUrl(String dowonUrl);

    //更新
    int updateInfo(TitleDetail detail);

    TitleDetail queryByUpdate();

    void updateActor(TitleDetail detail);

    /**
     * 更新标签
     */
    int updateType(@Param("id") int id,@Param("types") String types);
}
