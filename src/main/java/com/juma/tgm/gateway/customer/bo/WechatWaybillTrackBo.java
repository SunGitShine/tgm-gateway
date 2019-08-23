package com.juma.tgm.gateway.customer.bo;

import com.alibaba.fastjson.annotation.JSONField;
import com.juma.tgm.common.DateUtil;
import com.juma.tgm.driver.domain.Driver;
import com.juma.tgm.gateway.customer.vo.TrackDetailVo;
import com.juma.tgm.truck.domain.Truck;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillOperateTrack;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum;
import com.juma.tgm.waybill.vo.WaybillOperateTrackQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateType;
import com.juma.tgm.waybill.vo.WaybillOperateTrackQuery;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateFormatUtils;

/**
 * 微信端运单跟踪bo
 *
 * @ClassName: WechatWaybillTrackBo
 * @Description:
 * @author: liang
 * @date: 2017-06-22 18:11
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
public class WechatWaybillTrackBo {

    /**
     * 入城证
     */
    public static final String KEY_ENTRY_LICENSE = "entryLicense";

    /**
     * 司机评分
     */
    public static final String KEY_DRIVER_SCORE = "driverScore";

    /**
     * 车型
     */
    public static final String KEY_BOX_TYPE = "boxType";

    private Waybill waybill;

    private Driver driver;

    private Truck truck;


    @JSONField(serialize = false)
    private List<WaybillOperateTrackQuery> operateTracks;

    private List<TrackDetailVo> detailVoList = new ArrayList<>();

    private Map<String, Object> extraData;

    public WechatWaybillTrackBo(Waybill waybill, Driver driver, Truck truck, Map<String, Object> extraData,
        List<WaybillOperateTrackQuery> operateTracks) {
        this.waybill = waybill;
        this.driver = driver;
        this.truck = truck;
        this.extraData = extraData;
        this.operateTracks = operateTracks;
    }

    public Integer getTruckId() {
        return waybill.getTruckId();
    }

    public String getDriverName() {
        if (driver != null) {
            return driver.getNickname();
        }

        return null;
    }

    public String getPlateNumber() {
        if (truck != null) {
            return truck.getPlateNumber();
        }

        return null;
    }

    public Object getEntryLicenseType() {
        if (MapUtils.isEmpty(this.extraData)) return null;

        return this.extraData.get(WechatWaybillTrackBo.KEY_ENTRY_LICENSE);
    }

    public Object getDriverScore() {
        if (MapUtils.isEmpty(this.extraData)) return null;

        return this.extraData.get(WechatWaybillTrackBo.KEY_DRIVER_SCORE);
    }

    public Object getBoxType() {
        if (MapUtils.isEmpty(this.extraData)) return null;

        return this.extraData.get(WechatWaybillTrackBo.KEY_BOX_TYPE);
    }

    public Waybill getWaybill() {
        return waybill;
    }

    public void setWaybill(Waybill waybill) {
        this.waybill = waybill;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Truck getTruck() {
        return truck;
    }

    public void setTruck(Truck truck) {
        this.truck = truck;
    }

    public List<TrackDetailVo> getDetailVoList() {
        return detailVoList;
    }

    public void setDetailVoList(List<TrackDetailVo> detailVoList) {
        this.detailVoList = detailVoList;
    }

    public String getContent() {
        this.buildContent();

        return null;
    }

    private void buildContent() {

        //派车中：下单成功
        if (NumberUtils.compare(waybill.getStatusView(), Waybill.StatusView.WATING_RECEIVE.getCode()) == 0) {
            this.buildCreateTrack();
            return;
        }
        //待配送运单(未点击到仓):下单成功--派车成功--最新位置
        if (NumberUtils.compare(waybill.getStatusView(), Waybill.StatusView.WATING_DELIVERY.getCode()) == 0 && waybill.getArriveDepotTime() == null) {
            this.buildWaitDeliverBillInfo();
            return;
        }

        //未点击离仓
        if (NumberUtils.compare(waybill.getStatusView(), Waybill.StatusView.WATING_DELIVERY.getCode()) == 0 && waybill.getArriveDepotTime() != null && waybill.getDeliveryTime() == null) {
            this.buildArrivalBillInfo();
            return;
        }

        //配送中-司机已点击离仓运单
        if (NumberUtils.compare(waybill.getStatusView(), Waybill.StatusView.DELIVERYING.getCode()) == 0 && waybill.getArriveDepotTime() != null && waybill.getDeliveryTime() != null) {
            this.buildLeaveBillInfo();
            return;
        }

        //待支付：下单成功--派车成功--到仓时间--离仓时间--配送完成时间
        if (NumberUtils.compare(waybill.getStatusView(), Waybill.StatusView.WATING_PAY.getCode()) == 0) {
            this.buildCompleteDeliverBillInfo();
            return;
        }

        return;
    }


    /**
     * 下单成功
     */
    private void buildCreateTrack() {
        //派车中：下单成功
        if (CollectionUtils.isEmpty(operateTracks)) return;
        for (WaybillOperateTrack wot : operateTracks) {
            detailVoList.add(this.transformToDetail(wot));
        }
    }

    /**
     * 待配送运单
     */
    private void buildWaitDeliverBillInfo() {
        //待配送运单：下单成功--派车成功--最新位置；派车成功显示司机姓名和车牌号，可点击拨号直接联系司机
        if (CollectionUtils.isEmpty(operateTracks)) return;

        for (WaybillOperateTrack wot : operateTracks) {
            //如果有派车反馈删除
            if (NumberUtils.compare(wot.getOperateType(), OperateType.ASSIGN_FEED_BACK.getCode()) == 0) {
                continue;
            }
            detailVoList.add(this.transformToDetail(wot));
        }

    }

    /**
     * 配送中-司机未点击到仓运单
     */
    private void buildNoArrivalBillInfo() {
        //配送中-司机未点击到仓运单：下单成功--派车成功--最新位置
        if (CollectionUtils.isEmpty(operateTracks)) return;
        for (WaybillOperateTrack wot : operateTracks) {
            //如果有派车反馈删除
            if (NumberUtils.compare(wot.getOperateType(), OperateType.ASSIGN_FEED_BACK.getCode()) == 0) {
                continue;
            }
            detailVoList.add(this.transformToDetail(wot));
        }
    }

    /**
     * 配送中-司机已点击到仓运单
     */
    private void buildArrivalBillInfo() {
        //配送中-司机已点击到仓运单：下单成功--派车成功--到仓时间
        if (CollectionUtils.isEmpty(operateTracks)) return;
        for (WaybillOperateTrack wot : operateTracks) {
            //如果有派车反馈删除
            if (NumberUtils.compare(wot.getOperateType(), OperateType.ASSIGN_FEED_BACK.getCode()) == 0) {
                continue;
            }
            detailVoList.add(this.transformToDetail(wot));
        }
    }

    /**
     * 配送中-司机已点击离仓运单
     */
    private void buildLeaveBillInfo() {
        //配送中-司机已点击离仓运单：下单成功--派车成功--到仓时间--离仓时间--最新位置
        if (CollectionUtils.isEmpty(operateTracks)) return;
        for (WaybillOperateTrack wot : operateTracks) {
            //如果有派车反馈删除
            if (NumberUtils.compare(wot.getOperateType(), OperateType.ASSIGN_FEED_BACK.getCode()) == 0) {
                continue;
            }
            detailVoList.add(this.transformToDetail(wot));
        }
    }


    /**
     * 完成配送
     */
    private void buildCompleteDeliverBillInfo() {
        //待支付：下单成功--派车成功--到仓时间--离仓时间--配送完成时间
        if (CollectionUtils.isEmpty(operateTracks)) return;
        for (WaybillOperateTrack wot : operateTracks) {
            //如果有派车反馈删除
            if (NumberUtils.compare(wot.getOperateType(), OperateType.ASSIGN_FEED_BACK.getCode()) == 0) {
                continue;
            }
            detailVoList.add(this.transformToDetail(wot));
        }
    }


    private TrackDetailVo transformToDetail(WaybillOperateTrack operateTrack) {
        //1,4,5,6,7,8
        switch (operateTrack.getOperateType()) {
            case 1:
                return createBillToDetail(operateTrack);
            case 4:
                return assignedSys(operateTrack);
            case 5:
                return assignedSys(operateTrack);
            case 6:
                return arrivalEntrepot(operateTrack);
            case 7:
                return leaveEntrepot(operateTrack);
            case 8:
                return completeDeliver(operateTrack);
        }

        return new TrackDetailVo();
    }


    /**
     * 1下单
     *
     * @param operateTrack
     * @return
     */
    private TrackDetailVo createBillToDetail(WaybillOperateTrack operateTrack) {
        TrackDetailVo detailVo = new TrackDetailVo();
        detailVo.setContent("下单成功");
        detailVo.setTitle(this.dateFormat(operateTrack.getCreateTime().getTime()));
        detailVo.setOperationType(operateTrack.getOperateType());

        return detailVo;
    }

    /**
     * 4指派车辆
     *
     * @return
     */
    private TrackDetailVo assignedSys(WaybillOperateTrack operateTrack) {
        TrackDetailVo detailVo = new TrackDetailVo();
        detailVo.setTitle(this.dateFormat(operateTrack.getCreateTime().getTime()));
        detailVo.setContent("派车成功");
        detailVo.setOperationType(operateTrack.getOperateType());

        return detailVo;
    }

    /**
     * 6到仓
     *
     * @param operateTrack
     * @return
     */
    private TrackDetailVo arrivalEntrepot(WaybillOperateTrack operateTrack) {
        TrackDetailVo detailVo = new TrackDetailVo();
        detailVo.setTitle(this.dateFormat(operateTrack.getCreateTime().getTime()));
        detailVo.setContent("司机已到仓，开始装货");
        detailVo.setOperationType(operateTrack.getOperateType());

        return detailVo;
    }

    /**
     * 7离仓
     *
     * @param operateTrack
     * @return
     */
    private TrackDetailVo leaveEntrepot(WaybillOperateTrack operateTrack) {
        TrackDetailVo detailVo = new TrackDetailVo();
        detailVo.setTitle(this.dateFormat(operateTrack.getCreateTime().getTime()));
        detailVo.setContent("司机已离仓，开始配送");
        detailVo.setOperationType(operateTrack.getOperateType());

        return detailVo;
    }

    /**
     * 配送完成
     *
     * @param operateTrack
     * @return
     */
    private TrackDetailVo completeDeliver(WaybillOperateTrack operateTrack) {
        TrackDetailVo detailVo = new TrackDetailVo();
        detailVo.setTitle(this.dateFormat(operateTrack.getCreateTime().getTime()));
        detailVo.setContent("配送完成");
        detailVo.setOperationType(operateTrack.getOperateType());

        return detailVo;
    }

    /**
     * 统一格式化日期
     *
     * @param timestamp
     * @return
     */
    private String dateFormat(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        return DateFormatUtils.format(calendar.getTime(), DateUtil.YYYYMMDDHHMMSS);
    }
}
