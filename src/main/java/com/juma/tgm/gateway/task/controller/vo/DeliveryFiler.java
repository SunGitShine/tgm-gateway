package com.juma.tgm.gateway.task.controller.vo;

import java.io.Serializable;

/**
 * @description: ${description}
 *
 * @author: xieqiang
 *
 * @create: 2019-07-30 17:43
 **/
public class DeliveryFiler implements Serializable{

	private Integer projectId;

	private String linkName;

	private Integer backPageSize;

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getLinkName() {
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

	public Integer getBackPageSize() {
		return backPageSize;
	}

	public void setBackPageSize(Integer backPageSize) {
		this.backPageSize = backPageSize;
	}
}
