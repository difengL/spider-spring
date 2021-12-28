package com.self.spider.entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AvType {

    private int id;

    private String typeName;
    private String divide;
}
