package com.juma.tgm.gateway.reportInfo.controller;

import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.user.domain.LoginEcoUser;
import com.juma.conf.domain.ConfParamOption;
import com.juma.tgm.base.domain.BaseEnumDomian;
import com.juma.tgm.common.Constants;
import com.juma.tgm.common.DateUtil;
import com.juma.tgm.driver.domain.DriverLoginUser;
import com.juma.tgm.driver.domain.ReportInfoDetails;
import com.juma.tgm.driver.domain.ReportInfoParam;
import com.juma.tgm.reportInfo.service.ReportInfoDetailService;
import com.juma.tgm.reportInfo.service.ReportInfoService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author Libin.Wei
 * @version 1.0.0
 * @ClassName ReportInfoController.java
 * @Description 请填写注释...
 * @Date 2017年5月2日 上午11:53:24
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("report/info")
public class ReportInfoController {

	private final static Logger log = LoggerFactory.getLogger(ReportInfoController.class);

    @Resource
    private ReportInfoService reportInfoService;
    @Resource
    private ReportInfoDetailService reportInfoDetailService;

    /**
     * 路况报备
     */
    @ResponseBody
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public Date create(@RequestBody ReportInfoParam reportInfoParam, LoginEcoUser driverLoginEcoUser) {
        Date date = new Date();
        reportInfoParam.setFirstReportTime(date);
        reportInfoService.insertPageAndDetail(reportInfoParam, driverLoginEcoUser);
        return date;
    }

    /**
     * 路况报备详情列表
     */
    @ResponseBody
    @RequestMapping(value = "{waybillId}/details", method = RequestMethod.POST)
    public List<ReportInfoDetails> details(@PathVariable Integer waybillId, DriverLoginUser driverLoginUser,
            LoginEcoUser driverLoginEcoUser) {
        List<ReportInfoDetails> result = new ArrayList<ReportInfoDetails>();

        Map<String, String> mapRepotInfoType = new HashMap<String, String>();
        List<ConfParamOption> listRepotInfoType = reportInfoService.listRepotInfoType();
        for (ConfParamOption confParamOption : listRepotInfoType) {
            mapRepotInfoType.put(confParamOption.getOptionValue(), confParamOption.getOptionName());
        }

        List<ReportInfoDetails> list = reportInfoDetailService.listByWaybillId(waybillId, "desc");
        for (ReportInfoDetails reportInfoDetails : list) {
            if (reportInfoDetails.getReportInfoType() == 4) {
                continue;
            }
            reportInfoDetails.setReportInfoTypeView(mapRepotInfoType.get(reportInfoDetails.getReportInfoType() + ""));
            result.add(reportInfoDetails);
        }

        return result;
    }

    /**
     * 路况报备类型
     */
    @ResponseBody
    @RequestMapping(value = "type/list", method = RequestMethod.GET)
    public Object reportInfoTypeList() {
        List<BaseEnumDomian> result = new ArrayList<BaseEnumDomian>();
        for (ConfParamOption confParamOption : reportInfoService.listRepotInfoType()) {
            if ("4".equals(confParamOption.getOptionValue())) {
                continue;
            }
            BaseEnumDomian domain = new BaseEnumDomian();
            domain.setCode(Integer.parseInt(confParamOption.getOptionValue()));
            domain.setDesc(confParamOption.getOptionName());
            if (StringUtils.isNotBlank(confParamOption.getOptionDescribed())) {
                domain.setRequire(Constants.REQUIRED.equals(confParamOption.getOptionDescribed().trim()));
            }
            result.add(domain);
        }
        return result;
    }


	@ResponseBody
	@RequestMapping(value = "/waybillStatistics", method = RequestMethod.GET)
	public List<Object> waybillStatistics(@RequestParam String date, LoginEmployee loginEmployee){

    	//经纪人运费和单量
		String preDate = getPerDate(date);
		Map<String, Object> freightMap = new HashMap<>();
		freightMap.put("managerId", loginEmployee.getUserId());
		freightMap.put("areaCode", loginEmployee.getTenantId() + "_00");
		freightMap.put("date", date + "-01");
		freightMap.put("preDate", preDate + "-01");

		//货物收益分析top10
		Map<String, Object> incomeMap = new HashMap<>();
		incomeMap.put("tenantId", loginEmployee.getTenantId());
		incomeMap.put("yearMonth", date);

		//客户top排行
		Map<String, Object> customerMap = new HashMap<>();
		customerMap.put("areaCode", loginEmployee.getTenantId() + "_00");
		customerMap.put("date", date + "-01");

		return reportInfoService.waybillStatistics(date, freightMap, incomeMap, customerMap, loginEmployee);
	}

	public String getPerDate(String nowDate){
		Date date = DateUtil.parse(nowDate, "yyyy-MM");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date); // 设置为当前时间
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1); // 设置为上一个月
		date = calendar.getTime();

		return DateUtil.format(date, "yyyy-MM");
	}
}
