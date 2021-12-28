package com.self.spider.entities.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueryAvCondition {

    private int starNum;

    private String actor;

    private int pageSize;

    private String avName;

}
