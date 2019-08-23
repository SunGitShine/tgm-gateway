package com.juma.tgm.gateway.cargoOwner.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillDeliveryAddress;
import com.juma.tgm.waybill.domain.WaybillReceiveAddress;
import com.juma.tgm.waybill.domain.vo.ScatteredWaybillViewVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ScatteredWaybillCargoOwnerVo
 * @Description:
 * @author: liang
 * @date: 2017-11-16 10:10
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
public class ScatteredWaybillCargoOwnerVo {

    @JSONField(serialize = false)
    private ScatteredWaybillViewVo scatteredWaybillViewVo;

    /**
     * 当前登录人userId
     */
    @JSONField(serialize = false)
    private Integer userId;

    public ScatteredWaybillViewVo getScatteredWaybillViewVo() {
        return scatteredWaybillViewVo;
    }

    public void setScatteredWaybillViewVo(ScatteredWaybillViewVo scatteredWaybillViewVo) {
        this.scatteredWaybillViewVo = scatteredWaybillViewVo;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 运单列表数据
     *
     * @return
     */
    public Waybill getBillOverview() {
        if (this.getScatteredWaybillViewVo() == null) return null;
        if (this.getScatteredWaybillViewVo().getWaybill() == null) return null;

        Waybill original = this.getScatteredWaybillViewVo().getWaybill();
        Waybill billView = new Waybill();
        billView.setWaybillId(original.getWaybillId());
        billView.setPlanDeliveryTime(original.getPlanDeliveryTime());
        billView.setEstimateFreight(original.getEstimateFreight());
        billView.setBusinessBranch(original.getBusinessBranch());
        billView.setEstimateDistance(original.getEstimateDistance());
        billView.setCreateUserId(original.getCreateUserId());
        billView.setStatusView(original.getStatusView());
        billView.setStatus(original.getStatus());
        billView.setCreateTime(original.getCreateTime());

        return billView;
    }

    /**
     * 第一个是取货地
     * 第二个是末尾配送地
     *
     * @return
     */
    public List<WaybillDeliveryAddress> getAddressOverview() {
        if (this.getScatteredWaybillViewVo() == null) return null;
        if (CollectionUtils.isEmpty(this.getScatteredWaybillViewVo().getSrcAddress())) return null;
        if (CollectionUtils.isEmpty(this.getScatteredWaybillViewVo().getDestAddress())) return null;

        List<WaybillDeliveryAddress> finalData = new ArrayList<>();
        WaybillDeliveryAddress srcOriginal = this.getScatteredWaybillViewVo().getSrcAddress().get(0);
        srcOriginal.setAddressDetail(null);
        srcOriginal.setCityname(null);
//        srcOriginal.setContactName(null);
//        srcOriginal.setContactPhone(null);
        srcOriginal.setCoordinates(null);
        srcOriginal.setIsArrived(null);
        srcOriginal.setRegionCode(null);
        srcOriginal.setSimpleAddress(null);
        srcOriginal.setSpareContactPhone(null);
        srcOriginal.setCreateTime(null);
        srcOriginal.setCreateUserId(null);
        finalData.add(srcOriginal);

        List<WaybillReceiveAddress> destOriginals = this.getScatteredWaybillViewVo().getDestAddress();
        WaybillReceiveAddress destOriginal = destOriginals.get(0);
        WaybillDeliveryAddress destAddr = new WaybillDeliveryAddress();
        destAddr.setAddressName(destOriginal.getAddressName());
        destAddr.setAddressId(destOriginal.getAddressId());

        finalData.add(destAddr);

        return finalData;
    }

    /**
     * 是否平台代发
     *
     * @return
     */
    public Boolean isSelfPublish() {
        if (this.getScatteredWaybillViewVo() == null) return null;
        if (this.getScatteredWaybillViewVo().getWaybill() == null) return null;

        if (this.getUserId() == null) return null;

        Waybill waybill = this.getScatteredWaybillViewVo().getWaybill();
        if (NumberUtils.compare(this.getUserId(), waybill.getCreateUserId()) == 0) return true;

        return false;
    }

}
