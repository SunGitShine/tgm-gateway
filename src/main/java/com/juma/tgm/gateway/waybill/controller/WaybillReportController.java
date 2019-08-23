package com.juma.tgm.gateway.waybill.controller;

import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.user.domain.LoginEcoUser;
import com.juma.tgm.base.domain.MonthDomain;
import com.juma.tgm.driver.domain.DriverLoginUser;
import com.juma.tgm.waybill.domain.ReportForm;
import com.juma.tgm.waybill.domain.ReportQueryDomain;
import com.juma.tgm.waybillReport.domain.WaybillReport;
import com.juma.tgm.waybillReport.service.WaybillReportService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @ClassName WaybillReportController.java
 * @Description 统计报表
 * @author Libin.Wei
 * @Date 2017年2月6日 上午11:12:17
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Deprecated
@Controller
@RequestMapping("waybill")
public class WaybillReportController {

    @Resource
    private WaybillReportService waybillReportService;

    /**
     * 客户经理端运费报表
     */
    @ResponseBody
    @RequestMapping(value = "customerReport", method = RequestMethod.POST)
    public ReportForm customerReport(@RequestBody PageCondition pageCondition, LoginEmployee loginEmployee) {
        pageCondition.setOrderBy("plan_delivery_time desc");
        return waybillReportService.getCustomerReport(pageCondition, loginEmployee);
    }

    /**
     * 客户经理端结算报表
     */
    @ResponseBody
    @RequestMapping(value = "customerAccountReport", method = RequestMethod.POST)
    public ReportForm customerAccountReport(@RequestBody PageCondition pageCondition,LoginEmployee loginEmployee) {
        pageCondition.getFilters().put("isCheckout", 1);
        pageCondition.setOrderBy("checkout_time desc");
        return waybillReportService.getCustomerReport(pageCondition, loginEmployee);
    }

    /**
     * 司机端报表
     */
    @ResponseBody
    @RequestMapping(value = "driverReport", method = RequestMethod.POST)
    public ReportForm driverReport(@RequestBody PageCondition pageCondition, DriverLoginUser driverLoginUser, LoginEcoUser driverLoginEcoUser) {
        return waybillReportService.getDriverReport(pageCondition, driverLoginUser);
    }

    /**
     * 司机端报表(司机端：驹马生态用户使用-车机专用)
     */
    @ResponseBody
    @RequestMapping(value = "income/statistics", method = RequestMethod.POST)
    public ReportForm income(@RequestBody PageCondition pageCondition,DriverLoginUser driverLoginUser, LoginEcoUser driverLoginEcoUser) {
        pageCondition.getFilters().put("client", "smartTruck");
        return waybillReportService.getDriverReport(pageCondition, driverLoginUser);
    }

    /**
     * 司机条件列表
     */
    @ResponseBody
    @RequestMapping(value = "driverQueryList", method = RequestMethod.POST)
    public ReportQueryDomain getDriverQuery(@RequestBody MonthDomain month, DriverLoginUser driverLoginUser, LoginEcoUser driverLoginEcoUser) {
        return waybillReportService.getDriverQuery(month, driverLoginUser.getDriverId());
    }

    /**
     * 月份(现在仅有驹马专车司机端在使用)
     */
    @Deprecated
    @ResponseBody
    @RequestMapping(value = "monthList", method = RequestMethod.GET)
    public MonthDomain getMonthList() {
        return waybillReportService.getMonthDomain();
    }

    /**
     * 客户经理端条件列表
     */
    @ResponseBody
    @RequestMapping(value = "customerQueryList", method = RequestMethod.POST)
    public ReportQueryDomain getCustomerQuery(@RequestBody MonthDomain month, LoginEmployee loginEmployee) {
        return waybillReportService.getCustomerQuery(month, loginEmployee);
    }

    /**
     * 司机数据统计
     */
    @ResponseBody
    @RequestMapping(value = "report/baseIncomeInfo", method = RequestMethod.GET)
    public WaybillReport baseIncomeInfo(LoginEcoUser driverLoginEcoUser) {
        return waybillReportService.findDriverIncomeInfo(driverLoginEcoUser);
    }
}
