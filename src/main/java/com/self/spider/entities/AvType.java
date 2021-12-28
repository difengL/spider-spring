package com.self.spider.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor //全参构造函数
@NoArgsConstructor  //无参构造函数
public class AvType {

    private int id;

    private String typeName;
    private String divide;
    private int sortNum;
}
