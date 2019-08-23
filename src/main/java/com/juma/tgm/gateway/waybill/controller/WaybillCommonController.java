package com.juma.tgm.gateway.waybill.controller;

import com.alibaba.fastjson.JSON;
import com.giants.common.exception.BusinessException;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.monitor.realtime.domain.TemperatureAgg;
import com.juma.monitor.support.service.TemperatureServer;
import com.juma.tgm.tools.service.VmsCommonService;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillOperateTrack;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateApplication;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateType;
import com.juma.tgm.waybill.service.WaybillOperateTrackService;
import com.juma.tgm.waybill.service.WaybillService;
import com.juma.vms.truck.domain.Truck;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "waybill")
public class WaybillCommonController {

    private static final Logger log = LoggerFactory.getLogger(WaybillCommonController.class);

    @Resource
    private TemperatureServer temperatureServer;

    @Resource
    private WaybillService waybillService;

    @Resource
    private WaybillOperateTrackService waybillOperateTrackService;

    @Resource
    private VmsCommonService vmsCommonService;

    /**
     * 
     * @Title: temperatureView   
     * @Description: 订单温度轨迹
     * @param: @param waybillId
     * @param: @param loginEmployee      
     * @return: void      
     * @throws
     */
    @RequestMapping(value = "{waybillId}/temperature/view")
    @ResponseBody
    public List<TemperatureAgg> temperatureView(@PathVariable Integer waybillId, LoginEmployee loginEmployee) {
        return buildTemperatureView(waybillId, loginEmployee);
    }

    private List<TemperatureAgg> buildTemperatureView(Integer waybillId, LoginEmployee loginEmployee) {
        Waybill waybill = waybillService.getWaybill(waybillId);
        if (waybill == null || waybill.getDeliveryTime() == null) {
            throw new BusinessException("WaybillNotFound", "errors.paramCanNotNullWithName", "运单");
        }


        Truck truck = vmsCommonService.loadTruckByPlateNumber(waybill.getPlateNumber());
        if (truck == null || null == truck.getVehicleId()) {
            throw new BusinessException("VehicleNotFound", "errors.paramCanNotNullWithName", "车辆");
        }

        long endTime = 0l;
        
        long startTime = waybill.getDeliveryTime().getTime();
        WaybillOperateTrack waybillOperateTrack =  waybillOperateTrackService.findOperateTrackBy(waybillId, OperateType.LEAVE_DEPOT.getCode(),
                OperateApplication.FRNCE.getCode());
        if (waybillOperateTrack != null) {
            //手动和电子围栏都为空
            if(startTime == 0 && waybillOperateTrack.getDeclareTime() == null) {
                throw new BusinessException("LeaveDepotIsNull", "errors.paramCanNotNullWithName", "到仓时间为空");
            }
            if(startTime == 0 && waybillOperateTrack.getDeclareTime() != null) {
                startTime = waybillOperateTrack.getDeclareTime().getTime();
            }
            if(startTime != 0 && waybillOperateTrack.getDeclareTime() != null) {
                //取较小者
                startTime = Math.min(startTime, waybillOperateTrack.getDeclareTime().getTime());
            }
        }
        Date end = waybill.getFinishTime();
        if(end == null) {
            /*Calendar c = Calendar.getInstance();
            c.setTimeInMillis(startTime);
            c.set(Calendar.DAY_OF_MONTH, 7);*/
            endTime = new Date().getTime();
        } else {
            endTime = end.getTime();
        }
        log.info("start time is " +startTime+ ",end time is "+endTime);
        log.info(JSON.toJSONString(new Date(startTime)));
        log.info(JSON.toJSONString(new Date(endTime)));
        log.info(JSON.toJSONString(truck.getVehicleId()));
        return temperatureServer.queryList(truck.getVehicleId(), startTime, endTime);
    }
}
