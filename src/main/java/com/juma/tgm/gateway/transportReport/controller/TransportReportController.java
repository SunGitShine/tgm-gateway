package com.juma.tgm.gateway.transportReport.controller;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.juma.tgm.common.Base62;
import com.juma.tgm.common.DateUtil;
import com.juma.tgm.transportReport.domain.TransportReport;
import com.juma.tgm.transportReport.service.TransportReportService;

/**
 * @ClassName TransportReport.java
 * @Description 运输报告
 * @author Libin.Wei
 * @Date 2018年8月15日 下午5:41:31
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("transportReport")
public class TransportReportController {

    @Resource
    private TransportReportService transportReportService;

    @ResponseBody
    @RequestMapping(value = "load", method = RequestMethod.GET)
    public TransportReport loadTransportReport(String key, String tenantkey, String queryTime, Integer pageNo,
            Integer pageSize) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(tenantkey)) {
            return null;
        }

        String startTime = DateUtil.dayAddStart(-1);
        String endTime = DateUtil.dayAddEnd(-1);
        if (StringUtils.isNotBlank(queryTime)) {
            Date date = DateUtil.parse(queryTime, DateUtil.YYYYMMDD);
            startTime = DateUtil.dayStartReturnStr(date);
            endTime = DateUtil.dayEnd(date);
        }

        if (null == pageNo) {
            pageNo = 1;
        }

        if (null == pageSize) {
            pageSize = 1000;
        }

        return transportReportService.loadTransportReport(Base62.decodeByDivide(key).intValue(),
                Base62.decodeByDivide(tenantkey).intValue(), startTime, endTime, pageNo, pageSize);
    }

    /**
     * 测试类
     */
    @RequestMapping("{urlKey}")
    public void testRed(@PathVariable String urlKey, HttpServletResponse response) throws Exception {
        if (StringUtils.isBlank(urlKey)) {
            response.sendRedirect("http://data-cube.jumaps.com/index.html#/home/");
            return;
        }
        response.sendRedirect("http://data-cube.jumaps.com/index.html#/home/?key=11jc&tenantkey=WG");
    }
}
