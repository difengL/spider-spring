package com.self.spider.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor //全参构造函数
@NoArgsConstructor  //无参构造函数
public class TitleDetail {

    private int id;

    private String name;

    private String actor;
    //字幕
    private String subtitle;

    private String mosaic;

    private String md5;

    private String dowonUrl;

    private String img;

    private int audit;

    private String types;

    private String tableName;


}
