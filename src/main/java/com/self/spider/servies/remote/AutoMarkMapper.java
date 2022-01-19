package com.self.spider.servies.remote;

import com.self.spider.entities.AutoMark;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AutoMarkMapper {

    List<AutoMark> queryAllMark();

}
