package com.juma.tgm.gateway.waybill.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.juma.tgm.crm.service.IncomeStatisticsService;

/**
 * @ClassName IncomeStatisticsController.java
 * @Description 运费统计
 * @author Libin.Wei
 * @Date 2016年12月27日 下午2:22:06
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("incomeStatistics")
public class IncomeStatisticsController {

    @Resource
    private IncomeStatisticsService incomeStatisticsService;

    /**
     * 定时任务：运费统计
     */
    @RequestMapping(value = "cron/freightStatistics")
    @ResponseBody
    public void freightStatistics() {
        incomeStatisticsService.insert(-7);
    }
}
