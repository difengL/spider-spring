package com.self.spider.controller;

import com.alibaba.fastjson.JSONObject;
import com.self.spider.entities.AvType;
import com.self.spider.entities.TitleDetail;
import com.self.spider.entities.dto.AvTypeDto;
import com.self.spider.entities.dto.PageInfo;
import com.self.spider.entities.dto.QueryAvCondition;
import com.self.spider.servies.remote.AvMapper;
import com.self.spider.servies.remote.TypeMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ListController {

    private int pageSize = 8;

    @Resource
    private AvMapper mapper;

    @Resource
    private TypeMapper typeMapper;

    @RequestMapping("/list")
    public ModelAndView login(HttpServletRequest request) {

        String pageNumStr= StringUtils.isNotBlank(request.getParameter("pageNum"))?request.getParameter("pageNum"):"1";

        String avName = request.getParameter("avName");
        String avActor = request.getParameter("avActor");
        String movieType = request.getParameter("movieType");
        if(StringUtils.isBlank(movieType)){
            movieType = "1";
        }

        int pageNum = Integer.parseInt(pageNumStr);


        //查询数据
        QueryAvCondition condition = QueryAvCondition.builder()
                .starNum((pageNum - 1 )*pageSize)
                .pageSize(pageSize)
                .actor(StringUtils.isNotBlank(avActor)?avActor.substring(avActor.indexOf(")")+1):null)
                .avName(avName)
                .tableName("2".equals(movieType)?"av_list_china":"av_list")
                .build();

        int totalSize = mapper.queryTotalPages(condition);
        int totalPages =  totalSize%pageSize==0?totalSize/pageSize:totalSize/pageSize+1;

        int offsetStar = pageNum%5==0?(pageNum/5-1)*5+1:(pageNum/5)*5+1;
        int offsetEnd = pageNum%5==0?((pageNum/5))*5:((pageNum/5+1))*5;
        PageInfo page = PageInfo.builder()
                .list(mapper.queryAll(condition))
                .hasPrevious(pageNum>1?true:false)
                .previousNum(pageNum-1)
                .totalPages(totalPages)
                .hasNext(pageNum<totalPages?true:false)
                .nextNum(pageNum+1)
                .pageNumber(pageNum)
                .pageSize(pageSize)
                .offsetStar(offsetStar)
                .offsetEnd(offsetEnd<=totalPages?offsetEnd:totalPages)
                .build();
        //查询所有去重的名称
        List<AvType> typeList = typeMapper.queryAllType();


        // mapper.queryAllActor().stream().filter(item -> StringUtils.isNotBlank(item)&&item.length()<=10).collect(Collectors.toList())
        ModelAndView mv = new ModelAndView();
        mv.addObject("page", page);
        mv.addObject("actorList",new ArrayList<>());
        mv.addObject("avName", avName);
        mv.addObject("movieType", movieType);
        if("1".equals(movieType)){
            mv.addObject("movieTypeName", "岛国");
        }else if("2".equals(movieType)){
            mv.addObject("movieTypeName", "国产");
        }else{
            mv.addObject("movieTypeName", "三级");
        }


        mv.addObject("avActor", avActor);
        mv.addObject("typeList", typeList);
        mv.setViewName("list.html");
        return mv;
    }

}
