package com.self.spider.controller;

import com.self.spider.entities.AvRule;
import com.self.spider.entities.AvType;
import com.self.spider.entities.TitleDetail;
import com.self.spider.entities.dto.AvTypeDto;
import com.self.spider.scheduled.thread.BT.ChinaScheduleTask;
import com.self.spider.scheduled.thread.BT.JapanScheduleTask;
import com.self.spider.scheduled.thread.BT.LimitScheduleTask;
import com.self.spider.servies.c5cbca7s.manager.CinfigManager;
import com.self.spider.servies.remote.AvMapper;
import com.self.spider.servies.remote.RuleMapper;
import com.self.spider.servies.remote.TypeMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    private JapanScheduleTask japanTask;

    @Resource
    private ChinaScheduleTask chinaTask;

    @Resource
    private LimitScheduleTask limitTask;


    @Resource
    private AvMapper mapper;

    @Resource
    private RuleMapper ruleMapper;


    private static final String SCRIPT_STR = "false";



    @ResponseBody
    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public String version() {
        return  SCRIPT_STR;
    }


    @RequestMapping("/manager")
    public ModelAndView manager(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        String lableType = request.getParameter("lableType");
        if(StringUtils.isBlank(lableType)||"1".equals(lableType)){
            mv.setViewName("redirect:/view");
        }else if("2".equals(lableType)){
            mv.setViewName("redirect:/addType");
        }else if("3".equals(lableType)){
            mv.setViewName("redirect:/spider");
        }else if("4".equals(lableType)){
            mv.setViewName("redirect:/addRule");
        }else{
            mv.setViewName("redirect:/list");
        }
        return mv;
    }



    @RequestMapping("/saveKeyWord")
    public ModelAndView saveKeyWord(HttpServletRequest request) {
        String keyWord = request.getParameter("keyWord");
        String marks = request.getParameter("marks");
        ModelAndView mv = new ModelAndView();
        mv.setViewName("redirect:/addRule");
        if(StringUtils.isBlank(keyWord)){
            return mv;
        }
        //关键字不为空，则保存
        ruleMapper.deleteByKeyWord(keyWord);
        ruleMapper.addKeyWord(AvRule.builder()
                .keyWord(keyWord)
                .markType(marks)
                .build());
        return mv;
    }

    @RequestMapping("/addRule")
    public ModelAndView addRule(HttpServletRequest request) {
        String keyWord = request.getParameter("keyWord");
        ModelAndView mv = new ModelAndView();
        mv.addObject("keyWord", keyWord);
        mv.addObject("types", formartMark());
        mv.setViewName("addRule.html");
        if(StringUtils.isBlank(keyWord)){
            return mv;
        }
        //根据关键字查询存在的标签
        AvRule rule = ruleMapper.queryByKeyWord(keyWord);
        boolean action = Objects.nonNull(rule) && StringUtils.isNotBlank(rule.getMarkType());
        if(action){
            mv.addObject("exsistMark", rule.getMarkType());
        }

        return mv;
    }

    @PostMapping("executeSpider")
    public ModelAndView executeSpider(String prefix,String dowonLoadMark,String num,String optradio,String moveType,String startNum) {
        CinfigManager.getInstons().setPrefix(prefix,true);
        CinfigManager.getInstons().setDowonLoadMark(dowonLoadMark);
        CinfigManager.getInstons().setNum(Integer.parseInt(num));
        CinfigManager.getInstons().setStartNum(Integer.parseInt(startNum));
        ModelAndView mv = new ModelAndView();
        mv.setViewName("redirect:/view");
        if("2".equals(optradio)){
            return mv;
        }
        //判断执行的类型
        if(StringUtils.isNotBlank(moveType)){
            Arrays.stream(moveType.split(",")).map(Integer::parseInt).forEach(type -> {
                if(type == 1 ){
                    japanTask.configureTasks();
                }
                if(type == 2 ){
                    chinaTask.configureTasks();
                }
                if(type == 3 ){
                    limitTask.configureTasks();
                }

            });
        }
        return mv;
    }

    @RequestMapping("/spider")
    public ModelAndView spider(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("spider", CinfigManager.getInstons());
        mv.setViewName("spider.html");
        return mv;
    }

    @RequestMapping("/addType")
    public ModelAndView addType(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        List<AvType> typeList = typeMapper.queryAllGroupType();
        mv.addObject("typeList", typeList);
        mv.setViewName("addType.html");
        return mv;
    }


    //查询一个未修改过名称的
    @RequestMapping("/view")
    public ModelAndView listUpdate(HttpServletRequest request) {
        TitleDetail detail = mapper.queryByUpdate();


        ModelAndView mv = new ModelAndView();
        mv.addObject("av", detail);
        mv.addObject("types", formartMark());
        mv.setViewName("update.html");
        return mv;
    }

    private List<AvTypeDto> formartMark(){
        List<AvTypeDto> dto = new ArrayList<>();
        //拆分type
        String typeName = "";
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
        return dto;
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
        mv.setViewName("redirect:/addRule");
        return mv;
    }

}
