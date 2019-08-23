package com.juma.tgm.gateway.customer.bo;

import com.juma.tgm.common.DateUtil;
import com.juma.tgm.waybill.domain.Waybill;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * 运单轨迹概览bo
 *
 * @ClassName: WaybilTrackOverview
 * @Description:
 * @author: liang
 * @date: 2017-06-19 13:43
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
public class WaybillTrackOverviewBo {

    private Waybill waybill;
    /**
     * 抢单超时时间
     */
    private long competeBillTimeLength;

    /**
     * 最后一次派车反馈时间
     */
    private Date lastFeedBackTime;

    /**
     * title
     */
    private String title;


    public WaybillTrackOverviewBo(Waybill waybill, long timeLength, Date lastFeedBackTime) {
        this.waybill = waybill;
        this.competeBillTimeLength = timeLength;
        this.lastFeedBackTime = lastFeedBackTime;
    }


    private String customerName;


    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatusName() {
        return Waybill.StatusView.getDescrr(waybill.getStatusView());
    }

    public int getStatusView() {
        return waybill.getStatusView();
    }


    public String getContent() {

        return this.buildContent();
    }

    public Integer getReceiveWay(){

        if(waybill == null) return null;

        return waybill.getReceiveWay();
    }


    public Integer getWaybillId() {
        return waybill.getWaybillId();
    }


    public Integer getTruckId() {
        return waybill.getTruckId();
    }

    public Date getCDate() {
        return new Date();
    }

    public Date getLastFeedBackTime() {
        return lastFeedBackTime;
    }

    private String buildContent() {
        //派车中-司机抢单运单
        if (NumberUtils.compare(waybill.getReceiveWay(), Waybill.ReceiveWay.RECEIVED.getCode()) == 0 && NumberUtils.compare(waybill.getStatusView(), Waybill.StatusView.WATING_RECEIVE.getCode()) == 0) {

            //-- 调度已反馈
            if (StringUtils.isNotBlank(waybill.getAssignCarFeedback())) {
                return getDispatcherHasFeedback();
            }

            if (this.isCompeting(waybill)) {
                //-- 抢单中 --
                return getCompetingBillInfo();
            } else if (!this.isCompeting(waybill)) {
                //-- 抢单超时
                return getCompeteTimeOutInfo();
            }
        }

        //派车中-后台指派运单--已添加派车反馈
        if (NumberUtils.compare(waybill.getReceiveWay(), Waybill.ReceiveWay.MANUAL_ASSIGN.getCode()) == 0 && NumberUtils.compare(waybill.getStatusView(), Waybill.StatusView.WATING_RECEIVE.getCode()) == 0 && lastFeedBackTime != null) {
            return this.getDispatcherAssignFeedbackBillInfo();
        }
        //派车中-后台指派运单
        if (NumberUtils.compare(waybill.getReceiveWay(), Waybill.ReceiveWay.MANUAL_ASSIGN.getCode()) == 0 && NumberUtils.compare(waybill.getStatusView(), Waybill.StatusView.WATING_RECEIVE.getCode()) == 0) {
            return getDispatcherAssignBillInfo();
        }

        //待配送运单
        if (NumberUtils.compare(waybill.getStatusView(), Waybill.StatusView.WATING_DELIVERY.getCode()) == 0) {
            return getWaitDeliveryBillInfo();
        }
        //配送中
        if (NumberUtils.compare(waybill.getStatusView(), Waybill.StatusView.DELIVERYING.getCode()) == 0) {

            if (waybill.getDeliveryTime() != null) {
                //-- 司机已点击离仓运单
                return getHasDeparturedBillInfo();
            }

            if (waybill.getArriveDepotTime() == null) {
                //-- 司机未点击到仓运单
                return getHasNotArrivalBillInfo();
            } else {
                //-- 司机已点击到仓运单
                return getHasArrivaBillInfo();
            }
        }

        return null;
    }

    /**
     * 超时时间
     *
     * @return
     */
    private String timeOutLengthMin() {
        try {
            return String.valueOf(this.competeBillTimeLength / (60 * 1000));
        } catch (Exception e) {
            return "10";
        }
    }

    /**
     * 是否还在抢单中
     *
     * @param waybill
     * @return
     */
    private boolean isCompeting(Waybill waybill) {
        long deadLine = waybill.getCreateTime().getTime() + competeBillTimeLength;
        long now = Calendar.getInstance().getTime().getTime();

        if (NumberUtils.compare(now, deadLine) == -1) {
            return true;
        }

        return false;
    }


    /**
     * 抢单中的订单
     *
     * @return
     */
    private String getCompetingBillInfo() {
        //下单时间 | 已推送给司机，司机抢单中（10min倒计时）
        StringBuffer sb = new StringBuffer();
        this.setTitle(this.dateFormat(waybill.getCreateTime().getTime()));
        sb.append("已推送给司机，司机抢单中");
//        sb.append(this.timeOutLengthMin());
//        sb.append("min倒计时）");

        return sb.toString();
    }

    /**
     * 抢单超时
     *
     * @return
     */
    private String getCompeteTimeOutInfo() {
        //下单时间 | 10min内无司机抢单，调度正在处理
        StringBuffer sb = new StringBuffer();
        this.setTitle(this.dateFormat(waybill.getCreateTime().getTime()));
        sb.append(this.timeOutLengthMin());
        sb.append("min内无司机抢单，调度正在处理");
        return sb.toString();
    }

    /**
     * 调度已反馈
     *
     * @return
     */
    private String getDispatcherHasFeedback() {
        //派车反馈展示最近的反馈记录
        //反馈时间 | 调度已反馈无法派车，请查看lastFeedBackTime
        StringBuffer sb = new StringBuffer();
        if (this.lastFeedBackTime != null) {
            this.setTitle(this.dateFormat(this.lastFeedBackTime.getTime()));
        }
        sb.append("调度已反馈无法派车，请查看");
        return sb.toString();
    }


    /**
     * 后台指派运单
     *
     * @return
     */
    private String getDispatcherAssignBillInfo() {
        //下单时间 | 调度指派中，请耐心等待
        StringBuffer sb = new StringBuffer();
        this.setTitle(this.dateFormat(waybill.getCreateTime().getTime()));
        sb.append("调度指派中，请耐心等待");
        return sb.toString();
    }

    /**
     * 后台指派运单，调度添加了反馈
     *
     * @return
     */
    private String getDispatcherAssignFeedbackBillInfo() {
        //最后反馈时间 | 调度添加了反馈
        StringBuffer sb = new StringBuffer();
        this.setTitle(this.dateFormat(this.lastFeedBackTime.getTime()));
        sb.append("调度添加了反馈");
        return sb.toString();
    }

    /**
     * 待配送运单
     *
     * @return
     */
    private String getWaitDeliveryBillInfo() {
        //最新位置 | （车辆的当前位置）
//        StringBuffer sb = new StringBuffer();
//        this.setTitle("最新位置");
//        return sb.toString();
        return null;
    }

    //配送中
    //配送中-- 司机未点击到仓运单
    private String getHasNotArrivalBillInfo() {
        //最新位置 | （车辆的当前位置）
//        StringBuffer sb = new StringBuffer();
//        this.setTitle("最新位置");
//        return sb.toString();
        return null;
    }

    //配送中-- 司机已点击到仓运单
    private String getHasArrivaBillInfo() {
        //到仓时间| 司机已到仓，开始装货
        StringBuffer sb = new StringBuffer();
        this.setTitle(this.dateFormat(waybill.getArriveDepotTime().getTime()));
        sb.append("司机已到仓，开始装货");
        return sb.toString();
    }

    //配送中-- 司机已点击离仓运单
    private String getHasDeparturedBillInfo() {
        // 最新位置 | （车辆的当前位置）
//        StringBuffer sb = new StringBuffer();
//        this.setTitle("最新位置");
//        return sb.toString();
        return null;
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
