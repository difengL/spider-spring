package com.self.spider.servies.remote;

import com.self.spider.entities.AvRule;
import com.self.spider.entities.AvType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RuleMapper {

    /**
     * 根据关键字查询
     */
    AvRule queryByKeyWord(String keyWord);

    void addKeyWord(AvRule avRule);

    /**
     * 根据关键字删除
     */
    void deleteByKeyWord(String keyWord);
}
