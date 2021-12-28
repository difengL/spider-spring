package com.self.spider.entities.business;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Catalogue {
    private String title;

    private String url;

}
