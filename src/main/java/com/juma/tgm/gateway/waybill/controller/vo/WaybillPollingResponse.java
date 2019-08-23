package com.juma.tgm.gateway.waybill.controller.vo;

import java.io.Serializable;

/**
 * @ClassName WaybillPollingResponse.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2017年6月12日 上午10:22:26
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class WaybillPollingResponse implements Serializable {

    private static final long serialVersionUID = 8030029143905367275L;
    private Integer statusView;
    private String assignCarFeedback;

    /**
     * 运单改价审核状态
     */
    private Integer updateFreightAuditStatus;

    public Integer getStatusView() {
        return statusView;
    }

    public void setStatusView(Integer statusView) {
        this.statusView = statusView;
    }

    public String getAssignCarFeedback() {
        return assignCarFeedback;
    }

    public void setAssignCarFeedback(String assignCarFeedback) {
        this.assignCarFeedback = assignCarFeedback;
    }

    public Integer getUpdateFreightAuditStatus() {
        return updateFreightAuditStatus;
    }

    public void setUpdateFreightAuditStatus(Integer updateFreightAuditStatus) {
        this.updateFreightAuditStatus = updateFreightAuditStatus;
    }
}
