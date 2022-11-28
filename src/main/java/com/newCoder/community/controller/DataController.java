package com.newCoder.community.controller;

import com.newCoder.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * @author lijie
 * @date 2022-11-22 17:45
 * @Desc
 */
@Controller
public class DataController {

    @Autowired
    private DataService dataService;

    @RequestMapping("/data")
    public String getDataPage(){
        return "/site/admin/data";
    }

    @PostMapping("/data/uv")
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date UVStartDate,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date UVEndDate, Model model){
        long count = dataService.recordUV(UVStartDate, UVEndDate);
        model.addAttribute("UVCount",count);
        model.addAttribute("UVStartDate",UVStartDate);
        model.addAttribute("UVEndDate",UVEndDate);
        return "forward:/data";
    }



    @PostMapping("/data/dau")
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date DAUStartDate,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date DAUEndDate,Model model){
        long count = dataService.recordDAU(DAUStartDate,DAUEndDate);
        model.addAttribute("DAUCount",count);
        model.addAttribute("DAUStartDate",DAUStartDate);
        model.addAttribute("DAUEndDate",DAUEndDate);
        return "forward:/data";
    }

}
