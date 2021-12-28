package com.self.spider.entities.dto;

import com.self.spider.entities.AvType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AvTypeDto {
    private String typeName;

    private List<AvType> typeList;
}
