package com.juma.tgm.gateway.waybill.controller.vo;

import java.io.Serializable;

/**
 * @author rx
 * @version V1.0
 * @Description:
 * @date 2016/05/20 14:53
 */
public class WaybillVo implements Serializable {

	private static final long serialVersionUID = -7309496933901499964L;
	/** 运单ID */
    private Integer waybillId;
    /** 付款方式 */
    private String payWay;
    /**运单号 */
    private String waybillNo;

    public Integer getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(Integer waybillId) {
        this.waybillId = waybillId;
    }

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

	public String getWaybillNo() {
		return waybillNo;
	}

	public void setWaybillNo(String waybillNo) {
		this.waybillNo = waybillNo;
	}

}
