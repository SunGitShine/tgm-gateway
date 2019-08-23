package com.juma.tgm.gateway.customer.vo;

import java.io.Serializable;

/**
 * @ClassName TruckCustomerInfoVo.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2017年6月29日 上午9:42:23
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class TruckCustomerInfoVo implements Serializable {

    private static final long serialVersionUID = -7201741237365564270L;

    private String mobile;

    private RegionVo region;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public RegionVo getRegion() {
        return region;
    }

    public void setRegion(RegionVo region) {
        this.region = region;
    }

}
