package com.juma.tgm.gateway.customer.WechatBusinessModule;

import com.giants.common.exception.BusinessException;
import com.juma.conf.domain.ConfParamOption;
import com.juma.conf.service.ConfParamService;
import com.juma.tgm.common.Constants;
import com.juma.tgm.driver.domain.Driver;
import com.juma.tgm.driver.service.DriverService;
import com.juma.tgm.gateway.customer.bo.WechatDeliveryAddressBo;
import com.juma.tgm.gateway.customer.bo.WechatWaybillTrackBo;
import com.juma.tgm.truck.domain.Truck;
import com.juma.tgm.truck.service.TruckService;
import com.juma.tgm.truck.service.TruckTypeService;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillDeliveryAddress;
import com.juma.tgm.waybill.domain.WaybillReceiveAddress;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateApplication;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateType;
import com.juma.tgm.waybill.service.WaybillDeliveryAddressService;
import com.juma.tgm.waybill.service.WaybillOperateTrackService;
import com.juma.tgm.waybill.service.WaybillReceiveAddressService;
import com.juma.tgm.waybill.vo.WaybillOperateTrackQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @ClassName: WaychatBusinessBuilder
 * @Description:
 * @author: liang
 * @date: 2017-06-22 18:08
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */

@Component
public class WechatBusinessBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(WechatBusinessBuilder.class);

    @Resource
    private WaybillOperateTrackService waybillOperateTrackService;
    @Resource
    private DriverService driverService;
    @Resource
    private TruckService truckService;
    @Resource
    private WaybillReceiveAddressService waybillReceiveAddressService;
    @Resource
    private WaybillDeliveryAddressService waybillDeliveryAddressService;
    @Resource
    private ConfParamService confParamService;
    @Resource
    private TruckTypeService truckTypeService;

    /**
     * 微信端需要展示的运单轨迹
     */
    public static final List<Integer> allOperationList = Collections.unmodifiableList(Arrays.asList(new Integer[]{
        OperateType.CREATE_WAYBILL.getCode(),
        OperateType.ASSIGNED_SYS.getCode(),
        OperateType.ARRIVE_DEPOT.getCode(),
        OperateType.LEAVE_DEPOT.getCode(),
        OperateType.DELIVERYING.getCode(),
        OperateType.RECEIVED.getCode()
    }));

    public List<WechatWaybillTrackBo> buildTrackDetailBo(Collection<Waybill> waybills, List<Integer> operatTypeList) {

        List<WechatWaybillTrackBo> finalList = new ArrayList<>();
        for (Waybill waybill : waybills) {
            //--车辆信息
            Truck truck = null;
            Driver driver = null;

            if (waybill.getTruckId() != null) {
                truck = truckService.getTruck(waybill.getTruckId());
            }
            //--司机信息
            if (waybill.getDriverId() != null) {
                driver = driverService.getDriver(waybill.getDriverId());
            }
            List<Integer> notIncludeapplicationList = Arrays.asList(
                    new Integer[]{
                            OperateApplication.FRNCE.getCode(),
                            OperateApplication.BACKGROUND_MAP_MONITOR.getCode()
                    });
            List<WaybillOperateTrackQuery> operateTrackList =
                waybillOperateTrackService.listByWaybillIdAndTypes(waybill.getWaybillId(), operatTypeList, notIncludeapplicationList);

            if (LOG.isDebugEnabled()) {
                LOG.debug("操作轨迹:waybillId:{}=====>{}", waybill.getWaybillId(), operateTrackList);
            }

            Map<String, Object> extraData = this.buildViewData(driver, truck);
            WechatWaybillTrackBo wwtb = new WechatWaybillTrackBo(waybill, driver, truck, extraData, operateTrackList);

            finalList.add(wwtb);
        }

        return finalList;

    }

    private Map<String, Object> buildViewData(Driver driver, Truck truck) {
        Map<String, Object> data = new HashMap<>();

        String driverScore = null;
        String entryLicense = null;
        String boxType = null;

        if (truck != null) {
            //入城证信息
            entryLicense = this.buildEntryLicenseCnName(truck.getEntryLicense().byteValue());
            //箱型
            try {
                boxType = truckTypeService.findVehicleBoxTypeName(truck.getVehicleBoxType());
            } catch (BusinessException e) {
                //忽略
            }
        }

        data.put(WechatWaybillTrackBo.KEY_BOX_TYPE, boxType);
        data.put(WechatWaybillTrackBo.KEY_DRIVER_SCORE, driverScore);
        data.put(WechatWaybillTrackBo.KEY_ENTRY_LICENSE, entryLicense);

        return data;
    }

    private String buildEntryLicenseCnName(Byte entryLicense) {
        if (null == entryLicense) {
            return "无";
        }

        ConfParamOption option = null;
        try {
            option = confParamService.findParamOption(Constants.ENTRY_CITY_LICENSE_TYPE,
                    entryLicense.toString());
        } catch (BusinessException e) {
            //忽略
        }
        return (option == null ? "无" : option.getOptionName());
    }


    /**
     * 构造派车中运单配送地信息
     *
     * @param waybill
     * @return
     */
    public WechatDeliveryAddressBo buildDeliveryAddressBo(Waybill waybill) {

        //取货地信息
        List<WaybillReceiveAddress> receiveAddresses = waybillReceiveAddressService.findAllByWaybillId(waybill.getWaybillId());

        //配送地信息
        List<WaybillDeliveryAddress> deliverAddresses = waybillDeliveryAddressService.findAllByWaybillId(waybill.getWaybillId());

        return new WechatDeliveryAddressBo(receiveAddresses, deliverAddresses, waybill);
    }

}
