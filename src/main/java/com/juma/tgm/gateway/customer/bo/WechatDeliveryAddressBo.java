package com.juma.tgm.gateway.customer.bo;

import com.alibaba.fastjson.annotation.JSONField;
import com.juma.tgm.gateway.customer.vo.DeliveryAddressVo;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillDeliveryAddress;
import com.juma.tgm.waybill.domain.WaybillReceiveAddress;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用于展示 微信端 取货地送 送货地信息
 *
 * @ClassName: WechatDeliveryAddressBo
 * @Description:
 * @author: liang
 * @date: 2017-06-23 10:16
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
public class WechatDeliveryAddressBo {

    /**
     * 取货地信息
     */
    private List<DeliveryAddressVo> receiveAddress = new ArrayList<>();

    /**
     * 配送地信息
     */
    private List<DeliveryAddressVo> distributionAddress = new ArrayList<>();


    /**
     * 原始取货地信息
     */
    @JSONField(serialize = false)
    private List<WaybillReceiveAddress> wraList;

    /**
     * 原始配送地信息
     */
    @JSONField(serialize = false)
    private List<WaybillDeliveryAddress> wdaList;


    @JSONField(serialize = false)
    private Waybill waybill;

    public WechatDeliveryAddressBo(List<WaybillReceiveAddress> waybillReceiveAddress, List<WaybillDeliveryAddress> waybillDeliveryAddress, Waybill waybill) {
        this.wraList = waybillReceiveAddress;
        this.wdaList = waybillDeliveryAddress;
        this.waybill = waybill;
    }

    public List<DeliveryAddressVo> getReceiveAddress() {
        return receiveAddress;
    }

    public List<DeliveryAddressVo> getDistributionAddress() {
        return distributionAddress;
    }

    public Date getPlanDeliveryTime() {
        return waybill.getPlanDeliveryTime();
    }

    public String getWaybillNo() {
        return waybill.getWaybillNo();
    }

    public Date getCreateTime() {
        return waybill.getCreateTime();
    }


    public void getContent() {
        this.buildContent();
    }

    private void buildContent() {
        this.buildReceiveAddress(this.wraList);

        this.buildDistributionAddress(this.wdaList);
    }


    /**
     * 构造取货地信息
     * @param waybillReceiveAddress
     */
    private void buildReceiveAddress(List<WaybillReceiveAddress> waybillReceiveAddress) {
        if (CollectionUtils.isEmpty(waybillReceiveAddress)) return;

        DeliveryAddressVo vo = null;
        for (WaybillReceiveAddress wra : waybillReceiveAddress) {
            vo = new DeliveryAddressVo();
            vo.setAddress(wra.getAddressDetail());
            vo.setCellPhone(wra.getContactPhone());
            vo.setContact(wra.getContactName());

            receiveAddress.add(vo);
        }

    }

    /**
     * 构造配送地信息
     * @param waybillDeliveryAddress
     */
    private void buildDistributionAddress(List<WaybillDeliveryAddress> waybillDeliveryAddress) {
        if (CollectionUtils.isEmpty(waybillDeliveryAddress)) return;

        DeliveryAddressVo vo = null;
        for(WaybillDeliveryAddress wda : waybillDeliveryAddress){
            vo = new DeliveryAddressVo();
            vo.setContact(wda.getContactName());
            vo.setCellPhone(wda.getContactPhone());
            vo.setAddress(wda.getAddressDetail());

            distributionAddress.add(vo);
        }

    }
}
