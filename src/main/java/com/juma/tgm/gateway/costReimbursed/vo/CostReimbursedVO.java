package com.juma.tgm.gateway.costReimbursed.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.juma.tgm.costReimbursed.domain.CostReimbursed;

/**
 * @ClassName CostReimbursedVO.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2017年7月11日 下午2:06:44
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class CostReimbursedVO implements Serializable {

    private static final long serialVersionUID = -1695369665770470979L;
    private Integer waybillId;
    private List<CostReimbursed> listCostReimbursed = new ArrayList<CostReimbursed>();

    public Integer getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(Integer waybillId) {
        this.waybillId = waybillId;
    }

    public List<CostReimbursed> getListCostReimbursed() {
        return listCostReimbursed;
    }

    public void setListCostReimbursed(List<CostReimbursed> listCostReimbursed) {
        this.listCostReimbursed = listCostReimbursed;
    }

}
