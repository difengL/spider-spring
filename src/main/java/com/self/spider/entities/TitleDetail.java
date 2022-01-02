package com.self.spider.entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
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

    //private String tableName;


}
