package com.juma.tgm.gateway.reportInfo.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName ReportInfoDetailsVO.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2017年5月2日 下午5:52:59
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class ReportInfoDetailsVO implements Serializable {

    private static final long serialVersionUID = -3624665665069386568L;
    /** 上报时间列表 */
    private List<Date> reportTime;

    public List<Date> getReportTime() {
        return reportTime;
    }

    public void setReportTime(List<Date> reportTime) {
        this.reportTime = reportTime;
    }
}
