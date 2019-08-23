package com.juma.tgm.gateway.ckeckGoods.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName CheckGoodsVO.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2018年7月26日 上午11:17:44
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class CheckGoodsVO {

    private Integer waybillId;

    /**
     * 图片集合
     */
    private List<String> imgUrls = new ArrayList<String>();

    public Integer getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(Integer waybillId) {
        this.waybillId = waybillId;
    }

    public List<String> getImgUrls() {
        return imgUrls;
    }

    public void setImgUrls(List<String> imgUrls) {
        this.imgUrls = imgUrls;
    }

}
