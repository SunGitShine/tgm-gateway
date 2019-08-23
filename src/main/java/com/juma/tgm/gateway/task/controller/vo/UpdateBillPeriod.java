package com.juma.tgm.gateway.task.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "修改账期")
public class UpdateBillPeriod {

    @ApiModelProperty(value = "任务id")
    private Integer taskId;

    @ApiModelProperty(value = "承运商id")
    private Integer vendorId;

    @ApiModelProperty(value = "账期")
    private Integer billPeriod;

    @ApiModelProperty(value = "账期原因")
    private String billPeriodReason;

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public Integer getBillPeriod() {
        return billPeriod;
    }

    public void setBillPeriod(Integer billPeriod) {
        this.billPeriod = billPeriod;
    }

    public String getBillPeriodReason() {
        return billPeriodReason;
    }

    public void setBillPeriodReason(String billPeriodReason) {
        this.billPeriodReason = billPeriodReason;
    }
}
