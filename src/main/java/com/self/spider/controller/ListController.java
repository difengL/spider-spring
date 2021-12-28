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

        int pageNum = Integer.parseInt(pageNumStr);


        //查询数据
        QueryAvCondition condition = QueryAvCondition.builder()
                .starNum((pageNum - 1 )*pageSize)
                .pageSize(pageSize)
                .actor(StringUtils.isNotBlank(avActor)?avActor.substring(avActor.indexOf(")")+1):null)
                .avName(avName)
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



        ModelAndView mv = new ModelAndView();
        mv.addObject("page", page);
        mv.addObject("actorList", mapper.queryAllActor().stream().filter(item -> StringUtils.isNotBlank(item)&&item.length()<=10).collect(Collectors.toList()));
        mv.addObject("avName", avName);
        mv.addObject("avActor", avActor);
        mv.addObject("typeList", typeList);
        mv.setViewName("list.html");
        return mv;
    }


    //查询一个未修改过名称的
    @RequestMapping("/view")
    public ModelAndView listUpdate(HttpServletRequest request) {
        TitleDetail detail = mapper.queryByUpdate();
        //拆分type
        String typeName = "";
        List<AvTypeDto> dto = new ArrayList<>();

        List<AvType> other = new ArrayList<>();
        List<AvType> typeList = new ArrayList<>();

        List<AvType> list = typeMapper.queryAllType();
        for (int i = 0; i < list.size(); i++) {
            AvType typeDetail = list.get(i);
            if(StringUtils.isNotBlank(typeDetail.getDivide())){
                if(!typeDetail.getDivide().equals(typeName)){
                    typeName = typeDetail.getDivide();
                    typeList = new ArrayList<>();
                    dto.add( AvTypeDto.builder()
                            .typeName(typeName)
                            .typeList(typeList)
                            .build());
                }
                typeList.add(typeDetail);
            }else{
                other.add(typeDetail);
            }

        }
        //处理未分类的
        for (int i = 0; i < dto.size(); i++) {
            if("其他".equals(dto.get(i).getTypeName())){
                dto.get(i).getTypeList().addAll(other);
            }
        }
        System.out.println(JSONObject.toJSONString(dto));
        ModelAndView mv = new ModelAndView();
        mv.addObject("av", detail);
        mv.addObject("types", dto);
        mv.setViewName("update.html");
        return mv;
    }

    @RequestMapping("/update")
    public ModelAndView update(HttpServletRequest request) {

        String marks = request.getParameter("marks");
        String id = request.getParameter("id");
        String actor = request.getParameter("actor");
        TitleDetail detail =TitleDetail.builder()
                .id(Integer.parseInt(id))
                .types(marks)
                .actor(actor)
                .build();
        mapper.updateActor(detail);
        ModelAndView mv = new ModelAndView();
        mv.setViewName("redirect:/view");
        return mv;
    }


}
