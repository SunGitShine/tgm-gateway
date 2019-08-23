package com.juma.tgm.gateway.customer.CustomerManagerBusinessModule;

import com.juma.tgm.driver.domain.Driver;
import com.juma.tgm.driver.service.DriverService;
import com.juma.tgm.gateway.customer.bo.WaybillTrackDetailBo;
import com.juma.tgm.truck.domain.Truck;
import com.juma.tgm.truck.service.TruckService;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateApplication;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateType;
import com.juma.tgm.waybill.service.WaybillOperateTrackService;
import com.juma.tgm.waybill.vo.WaybillOperateTrackQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

/**
 * @ClassName: WaybillTrackDetailBuilder
 * @Description:
 * @author: liang
 * @date: 2017-06-20 10:17
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
@Component
public class WaybillTrackDetailBuilder {

    @Resource
    private WaybillOperateTrackService waybillOperateTrackService;
    @Resource
    private DriverService driverService;
    @Resource
    private TruckService truckService;

//    //派车中-司机抢单运单
//    public static final List<Integer> competeBillOperationList = Arrays.asList(new Integer[]{1, 21});//调度处理中
//    //派车中-后台指派/客户端指派运单
//    public static final List<Integer> assignBillOperationList = Arrays.asList(new Integer[]{1});//调度处理中
//    //待配送运单
//    public static final List<Integer> waitDeliveryOperationList = Arrays.asList(new Integer[]{1, 4});//最新位置，司机姓名和车牌，电话
//    //配送中-司机未点击到仓运单
//    public static final List<Integer> deliveringNotArrivaOperationList = Arrays.asList(new Integer[]{1, 4});//最新位置
//    //配送中-司机已点击到仓运单
//    public static final List<Integer> deliveringArrivaOperationList = Arrays.asList(new Integer[]{1, 4, 6});
//    //配送中-司机已点击离仓运单
//    public static final List<Integer> deliveringLeaveOperationList = Arrays.asList(new Integer[]{1, 4, 6, 7});//最新位置


    public static final List<Integer> allOperationList = Collections.unmodifiableList(Arrays.asList(
        new Integer[]{
            OperateType.CREATE_WAYBILL.getCode(),
            OperateType.ASSIGNED_SYS.getCode(),
            OperateType.ARRIVE_DEPOT.getCode(),
            OperateType.LEAVE_DEPOT.getCode(),
            OperateType.ASSIGN_FEED_BACK.getCode(),
            OperateType.RECEIVED.getCode()
        }
    ));


    public WaybillTrackDetailBo buildTrackDetailBo(Waybill waybill, List<Integer> typePackage, long timeLength) {
        if (waybill == null) return null;

        List<Integer> notIncludeapplicationList = Arrays.asList(
                new Integer[]{
                        OperateApplication.FRNCE.getCode(),
                        OperateApplication.BACKGROUND_MAP_MONITOR.getCode()
                });
        List<WaybillOperateTrackQuery> list = waybillOperateTrackService.listByWaybillIdAndTypes(waybill.getWaybillId(),
            typePackage, notIncludeapplicationList);

        if (CollectionUtils.isEmpty(list)) {
            list = new ArrayList<>();
        }
        //运单司机
        Driver driver = null;
        Truck truck = null;
        if (waybill.getDriverId() != null) {
            driver = driverService.getDriver(waybill.getDriverId());
        }
        if (waybill.getTruckId() != null) {
            truck = truckService.getTruck(waybill.getTruckId());
        }

        WaybillTrackDetailBo trackDetailBo = new WaybillTrackDetailBo(waybill, list, driver, truck, timeLength);
        trackDetailBo.generateData();
        return trackDetailBo;

    }

}
