package com.self.spider.controller;

import com.alibaba.fastjson.JSONObject;
import com.self.spider.entities.AvType;
import com.self.spider.entities.TitleDetail;
import com.self.spider.entities.dto.AvTypeDto;
import com.self.spider.servies.remote.AvMapper;
import com.self.spider.servies.remote.TypeMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author:  liuyao20
 * Date: Created in 2021/12/28 12:21
 * Description：管理界面
 */
@Controller
public class ManageController {

    @Resource
    private TypeMapper typeMapper;

    @Resource
    private AvMapper mapper;

    @RequestMapping("/manager")
    public ModelAndView manager(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        String lableType = request.getParameter("lableType");
        if(StringUtils.isBlank(lableType)||"1".equals(lableType)){
            mv.setViewName("redirect:/view");
            return mv;
        }
        List<AvType> typeList = typeMapper.queryAllGroupType();
        mv.addObject("typeList", typeList);
        mv.setViewName("addType.html");
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


    @RequestMapping("/addMark")
    public ModelAndView addMark(HttpServletRequest request) {


        String sortNum = request.getParameter("sortNum");
        String divide = request.getParameter("divide");
        String typeName = request.getParameter("typeName");

        AvType avtYPE = AvType.builder()
                .divide(divide)
                .typeName(typeName)
                .sortNum(Integer.parseInt(sortNum))
                .build();

        typeMapper.addType(avtYPE);

        ModelAndView mv = new ModelAndView();
        mv.setViewName("redirect:/manager");
        return mv;
    }

}
