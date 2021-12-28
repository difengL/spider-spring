package com.self.spider.servies.remote;

import com.self.spider.entities.AvType;
import com.self.spider.entities.TitleDetail;
import com.self.spider.entities.dto.QueryAvCondition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TypeMapper {

    //查询所有类型
    List<AvType> queryAllType();

    List<AvType> queryAllGroupType();

    void addType(AvType avtYPE);
}
