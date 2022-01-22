package com.self.spider.toolkits;

import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

/**
 * Author:  liuyao20
 * Date: Created in 2022/1/22 11:44
 * Description：
 */
public class SpiderKies {

    //获取执行的表名字
    public static final Function<String,String> TABLE_NAME = type ->{
        String result = "av_list";
        if(StringUtils.isNotBlank(type)){
            if("1".equals(type)){
                result = "av_list";
            }else if("2".equals(type)){
                result = "av_list_china";
            }else if("3".equals(type)){
                result = "av_list_limit";
            }
        }
        return result;
    };

}
