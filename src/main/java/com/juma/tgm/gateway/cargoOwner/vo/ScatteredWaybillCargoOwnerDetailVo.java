package com.juma.tgm.gateway.cargoOwner.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.juma.tgm.common.Constants;
import com.juma.tgm.crm.domain.CustomerInfo;
import com.juma.tgm.imageUploadManage.domain.ImageUploadManage;
import com.juma.tgm.truck.domain.AdditionalFunction;
import com.juma.tgm.truck.domain.bo.DriverTruckInfoBo;
import com.juma.tgm.waybill.domain.*;
import com.juma.tgm.waybill.domain.vo.ScatteredWaybillViewVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 落地配运单详情vo
 *
 * @ClassName: ScatteredWaybillCargoOwnerDetailVo
 * @Description:
 * @author: liang
 * @date: 2017-11-16 14:36
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
public class ScatteredWaybillCargoOwnerDetailVo {

    @JSONField(serialize = false)
    private ScatteredWaybillViewVo scatteredWaybillViewVo;

    public ScatteredWaybillViewVo getScatteredWaybillViewVo() {
        return scatteredWaybillViewVo;
    }

    public void setScatteredWaybillViewVo(ScatteredWaybillViewVo scatteredWaybillViewVo) {
        this.scatteredWaybillViewVo = scatteredWaybillViewVo;
    }

    /**
     * 详情页数据
     *
     * @return
     */
    public Waybill getBillDetail() {
        if (this.getScatteredWaybillViewVo() == null) return null;
        if (this.getScatteredWaybillViewVo().getWaybill() == null) return null;

        Waybill original = this.getScatteredWaybillViewVo().getWaybill();
        Waybill billView = new Waybill();
        billView.setBusinessBranch(original.getBusinessBranch());
        billView.setWaybillId(original.getWaybillId());
        billView.setEstimateFreight(original.getEstimateFreight());
        billView.setPlanDeliveryTime(original.getPlanDeliveryTime());
        billView.setArriveDepotTime(original.getArriveDepotTime());
        billView.setDeliveryTime(original.getDeliveryTime());
        billView.setFinishTime(original.getFinishTime());
        billView.setWaybillNo(original.getWaybillNo());
        billView.setStatus(original.getStatus());
        billView.setStatusView(original.getStatusView());
        billView.setCreateTime(original.getCreateTime());
        billView.setEstimateDistance(original.getEstimateDistance());
        billView.setWaybillCancelRemark(original.getWaybillCancelRemark());
        billView.setCustomerName(original.getCustomerName());
        billView.setNeedReceipt(original.getNeedReceipt());
        billView.setUpdateFreightAuditStatus(original.getUpdateFreightAuditStatus());
        billView.setUpdateFreightAuditRemark(original.getUpdateFreightAuditRemark());
        billView.setFreightToBeAudited(original.getFreightToBeAudited());
        billView.setNeedDeliveryPointNote(original.getNeedDeliveryPointNote());
        CustomerInfo customerInfo = this.getScatteredWaybillViewVo().getCustomerInfo();
        if (customerInfo != null) {
            billView.setCustomerName(customerInfo.getCustomerName());
        }

        return billView;
    }

    /**
     * 取货地
     *
     * @return
     */
    public List<WaybillDeliveryAddress> getSrcAddress() {
        if (this.getScatteredWaybillViewVo() == null) return null;

        return this.getScatteredWaybillViewVo().getSrcAddress();
    }

    /**
     * 配送地
     *
     * @return
     */
    public List<WaybillReceiveAddress> getDestAddress() {
        if (this.getScatteredWaybillViewVo() == null) return null;

        return this.getScatteredWaybillViewVo().getDestAddress();
    }


    /**
     * 用车要求
     *
     * @return
     */
    public TruckRequire getTruckRequire() {
        if (this.getScatteredWaybillViewVo() == null) return null;
        if (this.getScatteredWaybillViewVo().getTruckRequire() == null) return null;

        return this.getScatteredWaybillViewVo().getTruckRequire();
    }

    /**
     * 其他要求
     *
     * @return
     */
    public String getOtherReuqire() {
        if (this.getScatteredWaybillViewVo() == null) return null;
        ScatteredWaybillViewVo scatteredWaybillVo = this.getScatteredWaybillViewVo();
        if (CollectionUtils.isEmpty(scatteredWaybillVo.getAdditionalFunctions())) return null;
        List<AdditionalFunction> allFuns = this.getScatteredWaybillViewVo().getAdditionalFunctions();
        StringBuilder sb = new StringBuilder("");

        for (AdditionalFunction fun : allFuns) {
            //代收货款需要展示具体数额
            sb.append(fun.getFunctionName());

            if (StringUtils.equals(fun.getFunctionKey(), AdditionalFunction.FunctionKeys.COLLECTION_PAYMENT.name())) {
                if (scatteredWaybillVo.getWaybillParam() != null && scatteredWaybillVo.getWaybillParam().getAgencyTakeFreight() != null) {
                    BigDecimal agencyTakeFreight = scatteredWaybillVo.getWaybillParam().getAgencyTakeFreight();
                    sb.append("(¥");
                    sb.append(Constants.DECIMAL_2_FORMAT.format(agencyTakeFreight));
                    sb.append(")");
                }
            }
            sb.append(";");
        }

        // 配送单、隔天配送
        Waybill waybill = scatteredWaybillViewVo.getWaybill();
        if (null != waybill) {
            if (null != waybill.getNeedDeliveryPointNote()
                    && NumberUtils.compare(waybill.getNeedDeliveryPointNote(), 1) == 0) {
                sb.append("配送单").append(";");
            }

            if (null != waybill.getOnlyLoadCargo() && NumberUtils.compare(waybill.getOnlyLoadCargo(), 1) == 0) {
                sb.append("隔天配送").append(";");
            }
        }

        return sb.toString();
    }

    /**
     * 获取司机车辆信息
     *
     * @return
     */
    public DriverTruckInfoBo getDriverTruckInfo() {
        if (this.getScatteredWaybillViewVo() == null) return null;
        ScatteredWaybillViewVo scatteredWaybillVo = this.getScatteredWaybillViewVo();

        return scatteredWaybillVo.getDriverTruckInfoBo();
    }

    /**
     * 获取代收货款信息
     *
     * @return
     */
    public WaybillParam getWaybillParam() {
        if (this.getScatteredWaybillViewVo() == null) return null;
        if (this.getScatteredWaybillViewVo().getWaybillParam() == null) return null;
        WaybillParam original = this.getScatteredWaybillViewVo().getWaybillParam();

        WaybillParam waybillParam = new WaybillParam();

        waybillParam.setParamId(original.getParamId());
        waybillParam.setAgencyTakeFreightStatus(original.getAgencyTakeFreightStatus());
        waybillParam.setAgencyTakeFreight(original.getAgencyTakeFreight());

        return waybillParam;
    }

    /**
     * 客服电话
     *
     * @return
     */
    public String getHotline() {
        if (this.getScatteredWaybillViewVo() == null) return null;
        if (StringUtils.isBlank(this.getScatteredWaybillViewVo().getHotline())) return null;

        return this.getScatteredWaybillViewVo().getHotline();

    }

    /**
     * 回单信息
     *
     * @return
     */
    public List<ImageUploadManage> getReceiptImages() {
        if (this.getScatteredWaybillViewVo() == null) return null;

        return this.getScatteredWaybillViewVo().getReceiptManageList();
    }


    /**
     * 箱型名称
     *
     * @return
     */
    public String getVehicleBoxTypeName() {
        if (this.getScatteredWaybillViewVo() == null) return null;

        return this.scatteredWaybillViewVo.getVehicleBoxTypeName();

    }

    /**
     * 获取派单倒计时
     *
     * @return
     */
    public String getAssignWaitingTime() {
        if (this.getScatteredWaybillViewVo() == null) return null;

        return this.getScatteredWaybillViewVo().getAssignWaitingTime();
    }

    /**
     * 用车要求车型
     * @return
     */
    public String getTruckTypeName() {
        if (this.getScatteredWaybillViewVo() == null) return null;

        return this.getScatteredWaybillViewVo().getTruckTypeName();
    }

}
