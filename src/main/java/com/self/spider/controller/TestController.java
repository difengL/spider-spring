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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


@Controller
public class TestController {

    private static String SCRIPT_STR = "false";


    @RequestMapping("/setVersion")
    public ModelAndView setVersion(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("redirect:/anjian");
        mv.addObject("switchStr", SCRIPT_STR);
        return mv;
    }


    @ResponseBody
    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public String version() {
        return  SCRIPT_STR;
    }


    @ResponseBody
    @RequestMapping(value = "/versionManager", method = RequestMethod.GET)
    public String versionManager(String switchStr) {
        SCRIPT_STR = switchStr;
        return  "success";
    }


}
