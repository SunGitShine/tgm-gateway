package com.juma.tgm.gateway.task.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel(value = "任务日历参数")
public class TaskCalendar {

    @ApiModelProperty(value = "任务id")
    private Integer taskId;

    @ApiModelProperty(value = "任务开始日期")
    private Date startDate;

    @ApiModelProperty(value = "是否包含头")
    private Boolean isIncludeHeader = true;

    public Boolean getIsIncludeHeader() {
        return isIncludeHeader;
    }

    public void setIsIncludeHeader(Boolean isIncludeHeader) {
        this.isIncludeHeader = isIncludeHeader;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}
