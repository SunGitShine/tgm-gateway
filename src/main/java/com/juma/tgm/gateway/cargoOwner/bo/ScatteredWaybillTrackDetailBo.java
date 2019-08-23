package com.juma.tgm.gateway.cargoOwner.bo;

import com.alibaba.fastjson.annotation.JSONField;
import com.juma.tgm.common.DateUtil;
import com.juma.tgm.driver.domain.Driver;
import com.juma.tgm.gateway.customer.vo.TrackDetailVo;
import com.juma.tgm.truck.domain.Truck;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillOperateTrack;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateType;
import com.juma.tgm.waybill.vo.WaybillOperateTrackQuery;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateFormatUtils;

/**
 * 运单轨迹详情bo
 *
 * @ClassName: WaybillTrackDetailBo
 * @Description:
 * @author: liang
 * @date: 2017-06-20 09:58
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
public class ScatteredWaybillTrackDetailBo {

    private Waybill waybill;

    private Driver driver;

    private Truck truck;

    @JSONField(serialize = false)
    private long expireTimeLength;

    @JSONField(serialize = false)
    private List<WaybillOperateTrackQuery> operateTracks;

    private List<TrackDetailVo> detailVoList = new ArrayList<>();

    private String customerName;

    public ScatteredWaybillTrackDetailBo(Waybill waybill, List<WaybillOperateTrackQuery> operateTracks, Driver driver,
        Truck truck, long expireTimeLength) {
        this.waybill = waybill;
        this.operateTracks = operateTracks;
        this.driver = driver;
        this.truck = truck;
        this.expireTimeLength = expireTimeLength;
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

    public String getContent() {
        this.buildContent();

        return null;
    }

    public Date getCDate() {
        return new Date();
    }

    public Waybill getWaybill() {
        return waybill;
    }

    public Driver getDriver() {
        return driver;
    }

    public Truck getTruck() {
        return truck;
    }

    public List<TrackDetailVo> getDetailVoList() {
        return detailVoList;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    private void buildContent() {

        //派车中
//        if (NumberUtils.compare(waybill.getStatusView(), Waybill.StatusView.WATING_RECEIVE.getCode()) == 0) {
        this.buildCompetionBillInfo();
//            return;
//        }
//        //待配送运单
//        if (NumberUtils.compare(waybill.getStatusView(), Waybill.StatusView.WATING_DELIVERY.getCode()) == 0) {
//            this.buildWaitDeliverBillInfo();
//            return;
//        }
//
//        //配送中-司机已点击离仓运单
//        if (NumberUtils.compare(waybill.getStatusView(), Waybill.StatusView.DELIVERYING.getCode()) == 0) {
//            this.buildLeaveBillInfo();
//            return;
//        }
//
////        //待付款
//        if (NumberUtils.compare(waybill.getStatusView(), Waybill.StatusView.WATING_PAY.getCode()) == 0) {
//            this.buildNoArrivalBillInfo();
//            return;
//        }
//        //配送中-司机已点击到仓运单
//        if (NumberUtils.compare(waybill.getStatusView(), Waybill.StatusView.DELIVERYING.getCode()) == 0 && waybill.getArriveDepotTime() != null) {
//            this.buildArrivalBillInfo();
//            return;
//        }

    }


    /**
     * 派车中-司机抢单运单
     */
    private void buildCompetionBillInfo() {
        //下单成功--调度处理中（若无人抢）--调度已反馈（若无法派车）
        if (CollectionUtils.isEmpty(operateTracks)) return;

        for (WaybillOperateTrack wot : operateTracks) {
            detailVoList.add(this.transformToDetail(wot));
        }
    }

    /**
     * 派车中-后台指派
     */
    private void buildBackAssignBillInfo() {
        //派车中-后台指派/客户端指派运单：下单成功；并提示“调度指派中，请耐心等待”
        if (CollectionUtils.isEmpty(operateTracks)) return;
        for (WaybillOperateTrack wot : operateTracks) {
            detailVoList.add(this.transformToDetail(wot));
        }

        if (CollectionUtils.isNotEmpty(detailVoList)) {
            //在没有派车反馈的情况下展示指派中
            if (detailVoList.size() == 1) {
                TrackDetailVo detailVo = detailVoList.get(0);
                String tip = "<br/>调度指派中，请耐心等待";
                detailVo.setContent(detailVo.getContent() + tip);
            }
        }
    }

    /**
     * 待配送运单
     */
    private void buildWaitDeliverBillInfo() {
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
     * 配送完成
     */
    public void buildDeliveryCompleteInfo() {
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
        //1,4,6,7,21,5
        switch (operateTrack.getOperateType()) {
            case 1:
                return createBillToDetail(operateTrack);
            case 4:
                return driverAnswer(operateTrack);
            case 6:
                return arrivalEntrepot(operateTrack);
            case 7:
                return leaveEntrepot(operateTrack);
            case 21:
                return assignFeedback(operateTrack);
            case 43:
                return driverAnswer(operateTrack);
            case 5:
                return driverAnswer(operateTrack);
            case 8:
                return completeDeliver(operateTrack);
            case 39:
                return aiMatchSuccess(operateTrack);
        }

        return null;
    }

    /**
     * 1下单
     *
     * @param operateTrack
     * @return
     */
    private TrackDetailVo createBillToDetail(WaybillOperateTrack operateTrack) {
        TrackDetailVo detailVo = new TrackDetailVo();
        String body = Waybill.WaybillSource.buildWaybillSourceStr( this.getWaybill().getWaybillSource());
        if( !body .endsWith("下单")) {
            body = body +"下单";
        }
        detailVo.setContent(  body +"成功");
        detailVo.setTitle(this.dateFormat(operateTrack.getDeclareTime().getTime()));
        detailVo.setOperationType(operateTrack.getOperateType());

        return detailVo;
    }


    /**
     * 自动派单
     *
     * @param operateTrack
     * @return
     */
    private TrackDetailVo aiMatchSuccess(WaybillOperateTrack operateTrack) {
        TrackDetailVo detailVo = new TrackDetailVo();
        detailVo.setTitle(this.dateFormat(operateTrack.getDeclareTime().getTime()));
        detailVo.setContent("派车成功,等待司机接单");
        detailVo.setOperationType(operateTrack.getOperateType());

        return detailVo;
    }

    /**
     * 司机确认收到派单
     *
     * @param operateTrack
     * @return
     */
    private TrackDetailVo driverAnswer(WaybillOperateTrack operateTrack) {
        TrackDetailVo detailVo = new TrackDetailVo();
        detailVo.setTitle(this.dateFormat(operateTrack.getDeclareTime().getTime()));
        String body =  Waybill.ReceiveWay.buildReceiveWayStr( this.getWaybill().getReceiveWay());
        body = body.replace("自主派车","指派车队");
        detailVo.setContent( body +"成功");
        detailVo.setOperationType(-10);//给前端 判断使用
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
        detailVo.setTitle(this.dateFormat(operateTrack.getDeclareTime().getTime()));
        detailVo.setContent("到达取货地");
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
        detailVo.setTitle(this.dateFormat(operateTrack.getDeclareTime().getTime()));
        detailVo.setContent("离开取货地");
        detailVo.setOperationType(operateTrack.getOperateType());

        return detailVo;
    }

    /**
     * 21派车反馈
     *
     * @param operateTrack
     * @return
     */
    private TrackDetailVo assignFeedback(WaybillOperateTrack operateTrack) {
        TrackDetailVo detailVo = new TrackDetailVo();
        detailVo.setTitle(this.dateFormat(operateTrack.getDeclareTime().getTime()));
        detailVo.setContent("调度已反馈");
        detailVo.setOperationType(operateTrack.getOperateType());

        return detailVo;
    }


    /**
     * 配送完成
     *
     * @return
     */
    private TrackDetailVo completeDeliver(WaybillOperateTrack operateTrack) {
        TrackDetailVo detailVo = new TrackDetailVo();
        detailVo.setTitle(this.dateFormat(operateTrack.getDeclareTime().getTime()));
        detailVo.setContent("结束配送");
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
