package com.juma.tgm.gateway.waybill.controller.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName PositionVo.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2017年5月19日 上午10:20:05
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class PositionVo implements Serializable {

    private static final long serialVersionUID = -3015958686799166391L;

    private List<Integer> truckIdList;

    // 2017-05-19 重构，暂时保留，以后版本联合前端去掉此参数
    @Deprecated
    private List<Integer> waybillIdList;

    public List<Integer> getTruckIdList() {
        return truckIdList;
    }

    public void setTruckIdList(List<Integer> truckIdList) {
        this.truckIdList = truckIdList;
    }

    public List<Integer> getWaybillIdList() {
        return waybillIdList;
    }

    public void setWaybillIdList(List<Integer> waybillIdList) {
        this.waybillIdList = waybillIdList;
    }

}
