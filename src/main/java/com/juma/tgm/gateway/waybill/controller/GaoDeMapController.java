package com.juma.tgm.gateway.waybill.controller;

import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateType;
import com.juma.tgm.waybill.vo.WaybillOperateTrackQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.giants.common.exception.BusinessException;
import com.giants.common.lang.exception.CategoryCodeFormatException;
import com.giants.common.lang.exception.CategoryCodeOutOfRangeException;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.user.domain.LoginEcoUser;
import com.juma.auth.user.domain.LoginUser;
import com.juma.conf.domain.Region;
import com.juma.conf.service.RegionService;
import com.juma.monitor.service.DeviceFilterService;
import com.juma.monitor.service.RealTimePositionService;
import com.juma.monitor.truck.domain.RealTimePosition;
import com.juma.monitor.truck.domain.vo.VehicleInfoVo;
import com.juma.tgm.basicTruckType.service.LocationService;
import com.juma.tgm.common.Constants;
import com.juma.tgm.gaode.domain.AddressComponent;
import com.juma.tgm.gaode.domain.AmapRegeoResponse;
import com.juma.tgm.gaode.domain.Aois;
import com.juma.tgm.gaode.domain.District;
import com.juma.tgm.gaode.domain.GaodeDistrictResponse;
import com.juma.tgm.gaode.domain.GaodeInputtipsResponse;
import com.juma.tgm.gaode.domain.GaodeKeywordsResponse;
import com.juma.tgm.gaode.domain.Position;
import com.juma.tgm.gaode.domain.Regeocode;
import com.juma.tgm.gateway.waybill.controller.vo.PositionVo;
import com.juma.tgm.redis.service.TemperatureAlertService;
import com.juma.tgm.region.service.RegionTgmService;
import com.juma.tgm.truck.domain.Truck;
import com.juma.tgm.truck.service.TruckService;
import com.juma.tgm.waybill.domain.CityAdressData;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillDeliveryAddress;
import com.juma.tgm.waybill.domain.WaybillReceiveAddress;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum;
import com.juma.tgm.waybill.service.GaoDeMapService;
import com.juma.tgm.waybill.service.WaybillDeliveryAddressService;
import com.juma.tgm.waybill.service.WaybillOperateTrackService;
import com.juma.tgm.waybill.service.WaybillParamService;
import com.juma.tgm.waybill.service.WaybillReceiveAddressService;
import com.juma.tgm.waybill.service.WaybillService;
import com.juma.tgm.waybill.vo.WaybillOperateTrackQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author rx
 * @version V1.0
 * @Description: 高德地图
 * @date 2016/05/23 17:01
 */
@Controller
@RequestMapping(value = "gaoDeMap")
public class GaoDeMapController {

    private final static Logger log = LoggerFactory.getLogger(GaoDeMapController.class);

    @Autowired
    private GaoDeMapService gaoDeMapService;
    @Resource
    private RegionService regionService;
    @Resource
    private TruckService truckService;
    @Resource
    private RegionTgmService regionTgmService;
    @Resource
    private RealTimePositionService realTimePositionService;
    @Resource
    private WaybillService waybillService;
    @Resource
    private WaybillOperateTrackService waybillOperateTrackService;
    /**
     * 收货地 service
     */
    @Resource
    private WaybillDeliveryAddressService waybillDeliveryAddressService;

    /**
     * 配送地 service
     */
    @Resource
    private WaybillReceiveAddressService waybillReceiveAddressService;

    /**
     * 车牌号设备号关系
     */
    @Resource
    private DeviceFilterService deviceFilterService;

    
    @Resource
    private TemperatureAlertService temperatureAlertService;
    
    /**
     * 生成地区
     */
    @RequestMapping(value = "region", method = RequestMethod.GET)
    @ResponseBody
    public void region() throws BusinessException, CategoryCodeFormatException, CategoryCodeOutOfRangeException {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(1);

        GaodeDistrictResponse response = gaoDeMapService.gaodeDistrict(null, null, 1);
        for (District district : response.getDistricts()) {
            // 省
            for (District district1 : district.getDistricts()) {
                log.info(district1.getName());
                province(district1.getName(), loginUser);
            }
        }
    }

    private void province(String province, LoginUser loginUser)
            throws BusinessException, CategoryCodeFormatException, CategoryCodeOutOfRangeException {
        GaodeDistrictResponse response = gaoDeMapService.gaodeDistrict(province, "province", 2);// "四川省",
        // "province"
        for (District district : response.getDistricts()) {
            log.info(district.getName());
            Region _region = new Region();
            _region.setParentRegionId(null);
            _region.setRegionName(district.getName());
            Region region = regionService.createRegion(_region, loginUser);
            jilian(district, region, loginUser);
        }
    }

    private void jilian(District district, Region parent, LoginUser loginUser)
            throws BusinessException, CategoryCodeFormatException, CategoryCodeOutOfRangeException {
        for (District _district : district.getDistricts()) {
            Region _region = new Region();
            _region.setParentRegionId(parent.getRegionId());
            _region.setRegionName(_district.getName());
            Region region = regionService.createRegion(_region, loginUser);
            jilian(_district, region, loginUser);
        }
    }

    /**
     * 根据城市和关键字查询相关地址
     */
    @RequestMapping(value = "selectKeyWords", method = RequestMethod.POST)
    @ResponseBody
    public GaodeKeywordsResponse selectKeyWords(@RequestBody CityAdressData data) {
        return gaoDeMapService.gaodeTips(data.getCity(), data.getAddress());
    }

    /**
     * 根据坐标和关键字查询相关地址
     */
    @RequestMapping(value = "selectInputKeyWords", method = RequestMethod.POST)
    @ResponseBody
    public GaodeInputtipsResponse selectInputKeyWords(@RequestBody CityAdressData data) {
        return gaoDeMapService.gaodeInputtips(data.getCoordinate(), data.getAddress());
    }

    /**
     * 客户经理端使用：根据车辆ID获取车辆最新位置信息
     */
    @RequestMapping(value = "{truckId}/manager/position", method = RequestMethod.GET)
    @ResponseBody
    public Position positionForManager(@PathVariable Integer truckId, LoginEmployee loginEmployee) {
        return buildPosition(truckId);
    }

    /**
     * 货主端使用：根据车辆ID获取车辆最新位置信息
     */
    @RequestMapping(value = "{truckId}/customer/position", method = RequestMethod.GET)
    @ResponseBody
    public Position positionForCustomer(@PathVariable Integer truckId) {
        return buildPosition(truckId);
    }


    /**
     * 经纪人端查看运单轨迹
     *
     * @param truckId
     * @param waybillId
     * @return
     */
    @RequestMapping(value = "manager/{truckId}/{waybillId}/position", method = RequestMethod.GET)
    @ResponseBody
    public Position customerManagerWaybillTrack(@PathVariable(value = "truckId") Integer truckId, @PathVariable(value = "waybillId") Integer waybillId) {
        //车辆位置信息
        Position position = this.buildPosition(truckId);
        //运单信息
        Waybill waybill = waybillService.getWaybill(waybillId);
        if (waybill == null) return position;

        this.buildBillDeliveryTime(waybillId, position, waybill);

        position.setWaybillStatusView(waybill.getStatusView());

        this.buildDeliveryPoint(waybill, position);

        // 通过车辆id获取车牌号
        Truck truck = truckService.getTruck(waybill.getTruckId());
        if (truck == null)
            return position;

        Float[] minMax = temperatureAlertService.getMinMaxTemperatureByPlateNumber(waybill.getPlateNumber(), waybill.getTenantId(),waybill.getTenantCode());
        
        position.setRequiredMinTemperature(minMax[0]);
        position.setRequiredMaxTemperature(minMax[1]);
        
        // 通过车牌号获取设备号
        VehicleInfoVo vehicle = deviceFilterService.getVechicleByPlateNumber(truck.getPlateNumber());

        if (vehicle == null) return position;

        position.setDeviceNo(vehicle.getDeviceNo());
        if (vehicle.getDevice() == null) return position;

        position.setDeviceType(vehicle.getDevice().getType());

        return position;
    }

    /**
     * 落地配
     * 货主运单轨迹
     * @param waybillId
     * @param truckId
     * @return
     */
    @RequestMapping(value = "cargoOwner/position/{waybillId}/{truckId}", method = RequestMethod.GET)
    @ResponseBody
    public Position customerWaybillTrack(@PathVariable(value = "waybillId") Integer waybillId, @PathVariable(value = "truckId") Integer truckId){
        return this.customerManagerWaybillTrack(truckId, waybillId);
    }

    /**
     * 设置配送点信息
     * @param waybill
     * @param position
     */
    private void buildDeliveryPoint(Waybill waybill, Position position) {
        if (waybill == null) return;
        if (position == null) return;

        //取货地
        WaybillDeliveryAddress src = waybillDeliveryAddressService.findByWaybillId(waybill.getWaybillId());
        if(src == null) return ;

        src.setAddressId(null);
        src.setSpareContactPhone(null);
        src.setSimpleAddress(null);
        src.setRegionCode(null);
        src.setIsArrived(null);
        src.setCityname(null);
        src.setContactName(null);
        src.setContactPhone(null);
        src.setSequence(null);
        src.setCreateTime(null);
        src.setCreateUserId(null);
        src.setWaybillId(null);
        src.setLastUpdateUserId(null);
        src.setLastUpdateTime(null);

        position.setSrcAddrs(src);
        //配送地
        List<WaybillReceiveAddress> dests = waybillReceiveAddressService.findAllByWaybillId(waybill.getWaybillId());
        if(CollectionUtils.isEmpty(dests)) return ;

        List<WaybillReceiveAddress> destAddrs = new ArrayList<>();
        for(WaybillReceiveAddress dest: dests){
            dest.setAddressId(null);
            dest.setSimpleAddress(null);
            dest.setRegionCode(null);
            dest.setIsArrived(null);
            dest.setCityname(null);
            dest.setContactName(null);
            dest.setContactPhone(null);
            dest.setSequence(null);
            dest.setCreateTime(null);
            dest.setCreateUserId(null);
            dest.setWaybillId(null);
            dest.setLastUpdateUserId(null);
            dest.setLastUpdateTime(null);

            destAddrs.add(dest);
        }
        position.setDestAddrs(destAddrs);

    }

    /**
     * 获取运单开始配送时间
     * 如果配送中的运单信息中没有开始配送时间，
     * 则从操作轨迹中获取电子围栏触发时间作为开始配送时间
     *
     * @param waybillId
     * @param position
     * @param waybill
     */
    private void buildBillDeliveryTime(Integer waybillId, Position position, Waybill waybill) {

        if (waybill.getDeliveryTime() != null) {
            //运单有开始配送时间则使用运单时间
            position.setBeginTime(waybill.getDeliveryTime());
            position.setEndTime(new Date());
        } else {
            //运单没有开始配送时间使用电子围栏离仓事件时间
            List<Integer> allOperationList = Arrays.asList(new Integer[]{
                    OperateType.LEAVE_DEPOT.getCode()
            });
            List<WaybillOperateTrackQuery> operateTrackList =
                waybillOperateTrackService.listByWaybillIdAndTypes(waybillId, allOperationList, null);
            if (CollectionUtils.isNotEmpty(operateTrackList)) {
                position.setBeginTime(operateTrackList.get(0).getDeclareTime());
                position.setEndTime(new Date());
            }
        }
        //待支付运单
        if (NumberUtils.compare(waybill.getStatusView(), Waybill.StatusView.WATING_PAY.getCode()) == 0 || NumberUtils.compare(waybill.getStatusView(), Waybill.StatusView.FINISH.getCode()) == 0) {
            position.setEndTime(waybill.getFinishTime());
        }
    }

    // 构造位置信息
    private Position buildPosition(Integer truckId) {
        Position position = new Position();
        if (null == truckId) {
            return position;
        }

        // 获取车辆信息
        Truck truck = truckService.getTruck(truckId);
        if (null == truck) {
            return position;
        }

        // 在途监控接口获取坐标coordinates
        RealTimePosition realTimePosition = null;
        try {
            realTimePosition = realTimePositionService.queryLastPosByPlateNum(truck.getPlateNumber());
            log.info("在途监控：根据车牌号获取位置信息", JSON.toJSONString(realTimePosition));
        } catch (Exception e) {
            log.error("在途监控接口错误.", e);
        }
        if (null == realTimePosition) {
            return position;
        }
        Double lng = realTimePosition.getLng();
        Double lat = realTimePosition.getLat();
        if (null == lng || null == lat || lng.equals(0D) || lat.equals(0D)) {
            return position;
        }

        String coordinates = lng + "," + lat;

        // 调用高德获取位置格式化信息
        AmapRegeoResponse regeocode = gaoDeMapService.regeocode(coordinates);
        if (null == regeocode) {
            return position;
        }

        position.setGps(coordinates);
        position.setAddress(regeocode.getRegeocode().getFormattedAddress());
        return position;
    }

    /**
     * 根据车辆ID集合获取车辆最新位置信息
     */
    @RequestMapping(value = "customer/positionList", method = RequestMethod.POST)
    @ResponseBody
    public List<Position> positionList(@RequestBody PositionVo positionVo, LoginEcoUser cargoOwnerLoginEcoUser) {
        List<Position> positionList = new ArrayList<Position>();
        if (CollectionUtils.isEmpty(positionVo.getTruckIdList())) {
            return positionList;
        }

        for (Integer truckId : positionVo.getTruckIdList()) {
            Position position = buildPosition(truckId);
            position.setTruckId(truckId);
            positionList.add(position);
        }
        return positionList;
    }

    /**
     * 根据坐标获取位置信息 经济人端
     */
    @RequestMapping(value = "cityAddressInfo", method = RequestMethod.POST)
    @ResponseBody
    public CityAdressData cityAddressInfoByGps(@RequestBody CityAdressData cityAdressData, LoginEmployee loginEmployee) {
        return doGetCityAdressData(cityAdressData);
    }

    /**
     * 根据坐标获取位置信息 微信货主
     *
     * @param cityAdressData
     * @return
     */
    @RequestMapping(value = "cityAdressInfoForWx", method = RequestMethod.POST)
    @ResponseBody
    public CityAdressData getCityAddressInfoByGpsForWx(@RequestBody CityAdressData cityAdressData) {
        return doGetCityAdressData(cityAdressData);
    }

    /**
     * 根据坐标获取位置信息
     *
     * @param cityAdressData
     * @return
     */
    private CityAdressData doGetCityAdressData(@RequestBody CityAdressData cityAdressData) {
        if (StringUtils.isBlank(cityAdressData.getCoordinate())) {
            throw new BusinessException("notGetCoordinate", "maps.errors.notGetCoordinate");
        }

        // GPS转为高德坐标
        String coordinate = gaoDeMapService.convertCoordinate(cityAdressData.getCoordinate(),
                Constants.Coordsys.GPS.toString());

        // 根据坐标获取位置信息
        AmapRegeoResponse amapRegeoResponse = gaoDeMapService.regeocode(coordinate);
        if (null == amapRegeoResponse) {
            return cityAdressData;
        }
        Regeocode regeocode = amapRegeoResponse.getRegeocode();
        // 详细地址
        cityAdressData.setAddressDetail(regeocode.getFormattedAddress());

        // 所属兴趣点
        List<Aois> aois = regeocode.getAois();
        if (!aois.isEmpty() && aois.size() > 0) {
            cityAdressData.setAddress(aois.get(0).getName());
        }

        AddressComponent component = regeocode.getAddressComponent();
        // 获取城市编码
        String regionCode = regionTgmService.findRegionCodeBy(component);
        buildRegionCodeAndCity(cityAdressData, component, regionCode);
        return cityAdressData;
    }

    /**
     * 处理直辖市
     *
     * @return regionCode
     * @author Libin.Wei
     * @Date 2017年5月3日 上午11:03:27
     */
    private void buildRegionCodeAndCity(CityAdressData cityAdressData, AddressComponent component, String regionCode) {
        if (regionCode.startsWith("00")) {
            cityAdressData.setRegionCode("00000");
            cityAdressData.setCity(component.getProvince());
        } else if (regionCode.startsWith("01")) {
            cityAdressData.setRegionCode("01000");
            cityAdressData.setCity(component.getProvince());
        } else if (regionCode.startsWith("08")) {
            cityAdressData.setRegionCode("08000");
            cityAdressData.setCity(component.getProvince());
        } else if (regionCode.startsWith("21")) {
            cityAdressData.setRegionCode("21000");
            cityAdressData.setCity(component.getProvince());
        } else {
            cityAdressData.setRegionCode(regionCode.length() > 5 ? regionCode.substring(0, 5) : regionCode);
            cityAdressData.setCity(component.getCity());
        }
    }
}
