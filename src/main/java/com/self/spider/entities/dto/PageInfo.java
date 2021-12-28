package com.self.spider.entities.dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageInfo {

    private List list;

    //是否有上一页
    private boolean hasPrevious;

    //上一页
    private int previousNum;

    //总共多少页
    private int totalPages;

    //是否有下一页
    private boolean hasNext;

    private int nextNum;

    private int pageNumber;

    private int pageSize;

    private int offsetStar;

    private int offsetEnd;

}
