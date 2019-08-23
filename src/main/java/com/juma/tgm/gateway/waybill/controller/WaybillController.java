package com.juma.tgm.gateway.waybill.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.conf.domain.BusinessArea;
import com.juma.auth.conf.domain.BusinessAreaNode;
import com.juma.auth.conf.service.BusinessAreaService;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.user.domain.LoginEcoUser;
import com.juma.auth.user.domain.LoginUser;
import com.juma.conf.domain.Region;
import com.juma.server.vm.domain.dto.VehicleQueryConditionDTO;
import com.juma.server.vm.service.vehicle.AmsServiceV2;
import com.juma.server.vm.service.vehicle.DeviceBindService;
import com.juma.tgm.businessArea.service.TgmBusinessAreaService;
import com.juma.tgm.cityManage.domain.CityManage;
import com.juma.tgm.cityManage.domain.CityManageInfo;
import com.juma.tgm.cityManage.service.CityManageService;
import com.juma.tgm.common.Constants;
import com.juma.tgm.common.DateUtil;
import com.juma.tgm.crm.domain.CustomerInfo;
import com.juma.tgm.crm.domain.YesterdayIncomeInfo;
import com.juma.tgm.crm.service.CustomerInfoService;
import com.juma.tgm.customer.domain.CustomerLoginUser;
import com.juma.tgm.driver.domain.DriverLoginUser;
import com.juma.tgm.driver.domain.ReportInfo;
import com.juma.tgm.driver.domain.ReportInfoDetails;
import com.juma.tgm.gaode.domain.DriverLocation;
import com.juma.tgm.gateway.cargoOwner.CargoOwnerBusinessModule.ScatteredWaybillTrackDetailBuilder;
import com.juma.tgm.gateway.cargoOwner.bo.ScatteredWaybillTrackDetailBo;
import com.juma.tgm.gateway.common.BaseController;
import com.juma.tgm.gateway.waybill.controller.util.WaybillControllerUtil;
import com.juma.tgm.gateway.waybill.controller.util.WaybillQueryUtil;
import com.juma.tgm.gateway.waybill.controller.vo.AddressParamVo;
import com.juma.tgm.gateway.waybill.controller.vo.WaybillPollingResponse;
import com.juma.tgm.gateway.waybill.controller.vo.WaybillVo;
import com.juma.tgm.imageUploadManage.domain.ImageUploadManage;
import com.juma.tgm.landingWaybill.domain.AtFenceResultVo;
import com.juma.tgm.project.domain.RoadMap;
import com.juma.tgm.project.domain.ValuationWay;
import com.juma.tgm.receiptManage.service.ReceiptManageService;
import com.juma.tgm.region.service.RegionTgmService;
import com.juma.tgm.reportInfo.service.ReportInfoService;
import com.juma.tgm.scatteredWaybill.service.ScatteredWaybillService;
import com.juma.tgm.tools.service.VmsCommonService;
import com.juma.tgm.truck.domain.AdditionalFunction;
import com.juma.tgm.truck.domain.Truck;
import com.juma.tgm.truck.service.AdditionalFunctionService;
import com.juma.tgm.truck.service.TruckService;
import com.juma.tgm.waybill.domain.AddressHistory;
import com.juma.tgm.waybill.domain.AddressHistory.AddressType;
import com.juma.tgm.waybill.domain.CityAdressData;
import com.juma.tgm.waybill.domain.DistanceAndPriceData;
import com.juma.tgm.waybill.domain.TaxRate;
import com.juma.tgm.waybill.domain.ToAutoMatchWaybill;
import com.juma.tgm.waybill.domain.TruckRequire;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.Waybill.ReceiveWay;
import com.juma.tgm.waybill.domain.WaybillBindFence;
import com.juma.tgm.waybill.domain.WaybillBo;
import com.juma.tgm.waybill.domain.WaybillDeliveryAddress;
import com.juma.tgm.waybill.domain.WaybillDetailInfo;
import com.juma.tgm.waybill.domain.WaybillInfo;
import com.juma.tgm.waybill.domain.WaybillMonitor;
import com.juma.tgm.waybill.domain.WaybillParam;
import com.juma.tgm.waybill.domain.WaybillReceiveAddress;
import com.juma.tgm.waybill.domain.vo.DistanceAndPriceParamVo;
import com.juma.tgm.waybill.domain.vo.ScatteredWaybillCreateVo;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum;
import com.juma.tgm.waybill.service.AddressHistoryService;
import com.juma.tgm.waybill.service.DeliveryPointSupplementService;
import com.juma.tgm.waybill.service.GaoDeMapService;
import com.juma.tgm.waybill.service.TaxRateService;
import com.juma.tgm.waybill.service.WaybillAutoFenceServicve;
import com.juma.tgm.waybill.service.WaybillAutoMatchService;
import com.juma.tgm.waybill.service.WaybillCommonService;
import com.juma.tgm.waybill.service.WaybillDeliveryAddressService;
import com.juma.tgm.waybill.service.WaybillOperateTrackService;
import com.juma.tgm.waybill.service.WaybillParamService;
import com.juma.tgm.waybill.service.WaybillReceiveAddressService;
import com.juma.tgm.waybill.service.WaybillService;
import com.juma.tgm.waybill.service.WaybillTrackService;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateApplication;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateType;
import com.juma.tgm.waybill.service.*;
import com.juma.tgm.waybillReport.service.WaybillReportService;
import com.juma.vms.driver.domain.Driver;
import com.juma.vms.driver.domain.DriverTenant;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "waybill")
public class WaybillController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(WaybillController.class);

    @Resource
    private WaybillAutoMatchService waybillAutoMatchService;
    @Resource
    private WaybillService waybillService;
    @Resource
    private WaybillOperateTrackService waybillOperateTrackService;
    @Resource
    private WaybillAutoFenceServicve waybillAutoFenceServicve;
    @Resource
    private AddressHistoryService addressHistoryService;
    @Resource
    private GaoDeMapService gaoDeMapService;
    @Resource
    private CityManageService cityManageService;
    @Resource
    private WaybillParamService waybillParamService;
    @Resource
    private WaybillReportService waybillReportService;
    @Resource
    private CustomerInfoService customerInfoService;
    @Resource
    private DeviceBindService deviceBindService;
    @Resource
    private TruckService truckService;
    @Resource
    private RegionTgmService regionTgmService;
    @Resource
    private WaybillDeliveryAddressService waybillDeliveryAddressService;
    @Resource
    private WaybillReceiveAddressService waybillReceiveAddressService;
    @Resource
    private ReceiptManageService receiptManageService;
    @Resource
    private ReportInfoService reportInfoService;
    @Resource
    private AdditionalFunctionService additionalFunctionService;

    @Resource
    private AmsServiceV2 amsServiceV2;

    @Resource
    private DeliveryPointSupplementService deliveryPointSupplementService;

    @Resource
    private WaybillQueryUtil waybillQueryUtil;

    @Resource
    private WaybillControllerUtil waybillControllerUtil;

    /**
     * 可分享的逻辑业务区域
     */
    @Resource
    private TgmBusinessAreaService tgmBusinessAreaService;
    @Resource
    private ScatteredWaybillService scatteredWaybillService;
    @Resource
    private WaybillCommonService waybillCommonService;
    @Resource
    private ScatteredWaybillTrackDetailBuilder scatteredWaybillTrackDetailBuilder;
    @Resource
    private BusinessAreaService businessAreaService;
    @Resource
    private TaxRateService taxRateService;
    @Resource
    private VmsCommonService vmsCommonService;
    @Resource
    private WaybillTrackService waybillTrackService;

    /**
     * @Description: 定时任务 更新2,3状态到4 配送中
     */
    @RequestMapping(value = "cron/updateStatusToDeliverying")
    @ResponseBody
    public void updateStatusToDeliverying(Waybill waybill) {
        waybillService.updateWaybillStatusToDeliverying(new Waybill(), Constants.SYS_LOGIN_USER);
    }

    /**
     * 司机端 运单池(数量) 区分租户
     */
    @RequestMapping(value = "poolCount")
    @ResponseBody
    public int poolCount(LoginEcoUser driverLoginEcoUser) {
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNo(0);
        pageCondition.setPageSize(1);
        pageCondition.getFilters().put("tenantId", driverLoginEcoUser.getTenantId());
        return waybillService.getAcceptableWaybillCount(pageCondition, driverLoginEcoUser);
    }

    /**
     * 司机端 运单池(待接单) 区分租户
     */
    @RequestMapping(value = "acceptable")
    @ResponseBody
    public Page<WaybillBo> acceptable(LoginEcoUser driverLoginEcoUser, @RequestBody PageCondition pageCondition) {
        pageCondition.getFilters().put("tenantId", driverLoginEcoUser.getTenantId());
        return waybillService.getPageForAcceptableWaybillList(pageCondition, driverLoginEcoUser);
    }

    /**
     * 司机端 任务列表 区分租户
     */
    @RequestMapping(value = "list")
    @ResponseBody
    public Page<WaybillBo> list(DriverLoginUser driverLoginUser, LoginEcoUser driverLoginEcoUser,
            @RequestBody PageCondition pageCondition) {
        List<Integer> statusViewList = new ArrayList<Integer>();
        statusViewList.add(0);
        statusViewList.add(1);
        statusViewList.add(2);
        statusViewList.add(3);
        statusViewList.add(4);
        statusViewList.add(5);
        statusViewList.add(6);
        if (pageCondition.getFilters().isEmpty()) {
            pageCondition.getFilters().put("statusViewList", statusViewList);
        }

        if (driverLoginUser.getDriverId() == null) {
            return new Page<WaybillBo>(pageCondition.getPageNo(), pageCondition.getPageSize(), 0,
                    new ArrayList<WaybillBo>());
        }

        pageCondition.getFilters().put("driverId", driverLoginUser.getDriverId());
        pageCondition.getFilters().put("tenantId", driverLoginEcoUser.getTenantId());
        return waybillService.getPageForTodoWaybillList(pageCondition, driverLoginEcoUser);
    }

    /**
     * 统一APP 司机端 运单池(数量)
     */
    @RequestMapping(value = "v2/poolCount")
    @ResponseBody
    public int poolCountV2(LoginEcoUser driverLoginEcoUser) {
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNo(0);
        pageCondition.setPageSize(1);
        return waybillService.getAcceptableWaybillCount(pageCondition, driverLoginEcoUser);
    }

    /**
     * 统一APP 司机端 运单池(待接单)
     */
    @RequestMapping(value = "v2/acceptable")
    @ResponseBody
    public Page<WaybillBo> acceptableV2(LoginEcoUser driverLoginEcoUser, @RequestBody PageCondition pageCondition) {
        return waybillService.getPageForAcceptableWaybillList(pageCondition, driverLoginEcoUser);
    }

    /**
     * 统一APP 司机端 任务列表
     */
    @RequestMapping(value = "v2/list")
    @ResponseBody
    public Page<WaybillBo> listV2(DriverLoginUser driverLoginUser, LoginEcoUser driverLoginEcoUser,
            @RequestBody PageCondition pageCondition) {
        List<Integer> statusViewList = new ArrayList<Integer>();
        statusViewList.add(0);
        statusViewList.add(1);
        statusViewList.add(2);
        statusViewList.add(3);
        statusViewList.add(4);
        statusViewList.add(5);
        statusViewList.add(6);
        if (pageCondition.getFilters().isEmpty()) {
            pageCondition.getFilters().put("statusViewList", statusViewList);
        }
        if (driverLoginUser.getDriverId() == null) {
            pageCondition.getFilters().put("driverId", -1);
        } else {
            pageCondition.getFilters().put("driverId", driverLoginUser.getDriverId());
        }

        // 对账加了个初始状态 为了不让司机端（app）变更，做的适配
        Page<WaybillBo> p = waybillService.getPageForTodoWaybillList(pageCondition, driverLoginEcoUser);
        if (p != null && p.getResults() != null && !p.getResults().isEmpty()) {
            for (WaybillBo w : p.getResults()) {
                w.setReconciliationStatus(w.getReconciliationStatus() == 0 ? 1 : w.getReconciliationStatus());
            }
        }
        return p;
    }

    /**
     * 运单详情(司机端：驹马生态用户使用)
     */
    @RequestMapping(value = "{waybillId}", method = RequestMethod.GET)
    @ResponseBody
    public Waybill get(@PathVariable Integer waybillId, DriverLoginUser driverLoginUser,
            LoginEcoUser driverLoginEcoUser) {
        if (waybillId == null) {
            throw new BusinessException("validationFailure", "errors.validation.failure");
        }
        // 埋点
        waybillService.saveWaybillViewHistory(waybillId, driverLoginUser.getDriverId());

        // 司机已阅读
        waybillParamService.driverReadWaybill(waybillId, driverLoginEcoUser);
        WaybillBo waybillBo = waybillService.findWaybillBo(waybillId, driverLoginEcoUser);
        this.handleValuationWayViewToDriver(waybillBo);
        return waybillBo;
    }

    // 处理司机的计费规则展示
    private void handleValuationWayViewToDriver(WaybillBo waybillBo) {
        List<ValuationWay> valuationWays = waybillBo.getValuationWays();

        boolean doComplete = true;
        for (ValuationWay valuationWay : valuationWays) {
            if (valuationWay.getValue() != null && "0".equals(valuationWay.getValue())
                    && !"initiateRate".equals(valuationWay.getLabelInputName())) {
                doComplete = false;
                break;
            }
        }

        StringBuilder bufferPrev = new StringBuilder();
        if (doComplete) {
            bufferPrev.append("按");
            for (ValuationWay valuationWay : valuationWays) {
                bufferPrev.append(valuationWay.getLabelName());
                bufferPrev.append("+");
            }
            if (bufferPrev.length() > 0) {
                bufferPrev.deleteCharAt(bufferPrev.length() - 1).append("计费");
                waybillBo.setValuationWayView(bufferPrev.toString());
            }
        } else {
            bufferPrev.append("按");
            for (ValuationWay valuationWay : valuationWays) {
                bufferPrev.append(valuationWay.getLabelName());
                bufferPrev.append("+");
            }
            if (bufferPrev.length() > 1) {
                bufferPrev.deleteCharAt(bufferPrev.length() - 1).append("计费，待填写工作量");
                waybillBo.setValuationWayView(bufferPrev.toString());
            }
        }
    }

    /**
     * 运单监控
     */
    @RequestMapping(value = "monitor/{waybillId}", method = RequestMethod.GET)
    @ResponseBody
    public WaybillMonitor minotor(@PathVariable String waybillId) {
        if (waybillId == null) {
            throw new BusinessException("validationFailure", "errors.validation.failure");
        }
        return waybillService.fetchWaybill(waybillId);
    }

    /**
     * 接单
     */
    @RequestMapping(value = "receive")
    @ResponseBody
    public void receive(@RequestBody Waybill waybill, LoginEcoUser driverLoginEcoUser) {
        waybillService.receiveWaybill(waybill, driverLoginEcoUser);

        // 设备绑定
        cellPhoneBind(waybill, driverLoginEcoUser);

        // 操作轨迹
        waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.RECEIVED,
                OperateApplication.DRIVER_SYS, buildTrackNotRequieParam(waybill, null),
                driverLoginEcoUser);

        // 绑定电子围栏
        waybillAutoFenceServicve.bindWaybillIdAndFenceId(waybill.getWaybillId(), WaybillBindFence.Sign.DELIVERY_ADDRESS,
                driverLoginEcoUser);
    }

    private void cellPhoneBind(Waybill waybill, LoginEcoUser loginEcoUser) {
        try {
            log.info("绑定设备设备号：{}", waybill.getDeviceNo());
            if (StringUtils.isNotBlank(waybill.getDeviceNo())
                    && (null == waybill.getDeviceType() || 2 == waybill.getDeviceType())) {
                Waybill wb = waybillService.getWaybill(waybill.getWaybillId());
                Truck truck = truckService.getTruck(wb.getTruckId());
                log.info("绑定设备开始truck：{}", truck.toString());
                VehicleQueryConditionDTO dto = new VehicleQueryConditionDTO();
                dto.setVehicleId(truck.getVehicleId());
                dto.setTenantId(loginEcoUser.getTenantId());
                com.juma.server.vm.domain.Driver amsDriver = amsServiceV2.getByBindVehicleId(dto);
                // 设备绑定
                deviceBindService.cellPhoneBind(waybill.getDeviceNo(), amsDriver.getAreaCode(), truck.getVehicleId(),
                        loginEcoUser.getUserId(), Constants.AUTH_KEY_TGM_MANAGE);
            }
        } catch (Exception e) {
            log.error("绑定设备失败：{}", e);
        }
    }

    /**
     * 客户经理确认收到运费
     */
    @RequestMapping(value = "payment", method = RequestMethod.POST)
    @ResponseBody
    public void payment(@RequestBody WaybillVo waybillVo, LoginEmployee loginEmployee) throws BusinessException {
        Waybill waybill = waybillService.getWaybillAndCheckExist(waybillVo.getWaybillId());
        waybillService.changeToPaied(waybillVo.getWaybillId(), loginEmployee);

        // 操作轨迹
        waybillOperateTrackService.insert(waybillVo.getWaybillId(), OperateType.PAIED,
                OperateApplication.CUSTOMER_SYS, null, loginEmployee);
    }

    /**
     * 即将配送的订单信息
     */
    @RequestMapping(value = "will/delivery")
    @ResponseBody
    public WaybillBo willDelivery(DriverLoginUser driverLoginUser, LoginEcoUser driverLoginEcoUser) {
        return waybillService.getDeliveriedWaybill(driverLoginUser.getDriverId(), driverLoginEcoUser);
    }

    @SuppressWarnings("unchecked")
    public static <T> T mergeObject(T first, T second) throws IllegalAccessException, InstantiationException {
        Class<?> clazz = first.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Object returnValue = clazz.newInstance();
        for (Field field : fields) {
            if (field.getModifiers() == 26) continue;
            field.setAccessible(true);
            Object value1 = field.get(first);
            Object value2 = field.get(second);
            Object value = (value1 != null) ? value1 : value2;
            field.set(returnValue, value);
        }
        return (T) returnValue;
    }

    /**
     * (标准计价接口+司机报价) 得到距离和价格信息
     * 
     * @throws Exception
     * @throws IllegalAccessException
     */
    @RequestMapping(value = "getDistanceAndPrice", method = RequestMethod.POST)
    @ResponseBody
    public DistanceAndPriceData getDistanceAndPrice(@RequestBody DistanceAndPriceParamVo dp,
            LoginEmployee loginEmployee) throws IllegalAccessException, Exception {
        // TODO 4.6.1之后删除
        if (null != dp.getTruckRequire() && null != dp.getTruckRequire().getTaxRateId()) {
            TaxRate taxRate = taxRateService.getTaxRate(dp.getTruckRequire().getTaxRateId());
            dp.getTruckRequire().setTaxRateValue(taxRate == null ? null : taxRate.getTaxRateValue());
        }

        DistanceAndPriceData rst = new DistanceAndPriceData();

        // 电子围栏判断
        AtFenceResultVo atFenceResult = scatteredWaybillService.isAtFenceArea(dp.getSrcAddress(), dp.getToAddress(),
                loginEmployee);

        // 预估距离判断
        this.buildRegionData(dp, rst, loginEmployee);

        boolean inCity = false;
        if (atFenceResult != null) {
            rst.setInBusinessArea(atFenceResult.getAtBusinessArea());
            rst.setInCity(atFenceResult.getAtCity());
            rst.setInForbiddenArea(atFenceResult.isAtForbiddenArea());
            rst.setForbiddenType(atFenceResult.getForBiddenType());
            rst.setForbiddenAreaIndex(atFenceResult.getForbiddenAreaIndex());
            inCity = atFenceResult.getAtCity();
        }

        Waybill waybill = dp.getWaybill();
        if (waybill == null) return rst;
        waybill.setEstimateDistance(rst.getDistance());
        waybill.setTolls(rst.getTolls());
        if (rst.getDistance() == null) return rst;
        if (dp.getTruckRequire() == null) return rst;
        TruckRequire truckRequire = dp.getTruckRequire();
        this.addEntryLicenseFunction(inCity, truckRequire);
        waybill.setRegionCode(rst.getRegionCode());
        if (StringUtils.isBlank(waybill.getRegionCode())) return rst;

        DistanceAndPriceData _rst = waybillCommonService.calculateStanderPriceWithDriverFreight(dp, loginEmployee);

        return mergeObject(_rst, rst);
    }

    /**
     * 专车 创建运单
     */
    @RequestMapping(value = "createWaybill", method = RequestMethod.POST)
    @ResponseBody
    public List<Integer> createWaybill(@RequestBody WaybillBo waybillBo, LoginEmployee loginEmployee) {
        if (waybillBo.getCreateBatchAmount() != null && NumberUtils.compare(waybillBo.getCreateBatchAmount(), 50) > 0) {
            throw new BusinessException("overMaxWaybillSize", "waybill.error.overMaxWaybillSize");
        }

        // 预计完成时间不能早于计划用车时间
        waybillControllerUtil.planEstimateFinishTimeCheck(waybillBo);

        waybillBo.getWaybill().setTest(loginEmployee.isTest());
        if (waybillBo.getCreateBatchAmount() == null || NumberUtils.compare(waybillBo.getCreateBatchAmount(), 1) <= 0) {
            waybillBo.setCreateBatchAmount(1);
        }

        List<Integer> ids = new ArrayList<Integer>();
        for (int i = 0; i < waybillBo.getCreateBatchAmount(); i++) {
            // 建单
            Integer id = waybillService.createWaybill(waybillBo, Waybill.WaybillSource.JUMA_CLIENT, loginEmployee);

            // 操作轨迹
            waybillOperateTrackService.insert(id, OperateType.CREATE_WAYBILL,
                    OperateApplication.CUSTOMER_SYS,
                    buildTrackNotRequieParam(waybillBo.getWaybill(),
                            ("首次派车方式:" + waybillBo.getWaybill().getReceiveWay())),
                    loginEmployee);

            // 指定车辆
            if (null != waybillBo.getWaybill().getReceiveWay() && waybillBo.getWaybill().getReceiveWay().equals(
                ReceiveWay.ASSIGNED.getCode())) {
                waybillOperateTrackService.insert(id, OperateType.ASSIGNED_SYS,
                    OperateApplication.CUSTOMER_SYS,
                    buildTrackNotRequieParam(waybillBo.getWaybill(), null), loginEmployee);
            }

            // 调度指派
            if (null != waybillBo.getWaybill().getReceiveWay() && waybillBo.getWaybill().getReceiveWay().equals(
                ReceiveWay.MANUAL_ASSIGN.getCode())) {
                waybillOperateTrackService.insert(id, OperateType.MANUAL_ASSIGN,
                    OperateApplication.CUSTOMER_SYS,
                    buildTrackNotRequieParam(waybillBo.getWaybill(), null), loginEmployee);
            }

            // 转承运商
            if (null != waybillBo.getWaybill().getReceiveWay() && waybillBo.getWaybill().getReceiveWay().equals(
                ReceiveWay.TRANSFORM_BILL.getCode())) {
                waybillOperateTrackService.insert(id, OperateType.TRANSFORM_BILL,
                    OperateApplication.CUSTOMER_SYS,
                    buildTrackNotRequieParam(waybillBo.getWaybill(), null), loginEmployee);
            }

            ids.add(id);
        }
        return ids;
    }

    /**
     * 运单加跑
     *
     * @param waybillBo
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "createWithDriver", method = RequestMethod.POST)
    @ResponseBody
    public Integer createWaybillWithDriver(@RequestBody WaybillBo waybillBo, LoginEmployee loginEmployee) {
        // 预计完成时间不能早于计划用车时间
        waybillControllerUtil.planEstimateFinishTimeCheck(waybillBo);

        Waybill waybill = waybillBo.getWaybill();

        waybill.setTest(loginEmployee.isTest());
        waybill.setReceiveWay(Waybill.ReceiveWay.ASSIGNED.getCode());

        Integer id = waybillService.createWaybillWithDriver(waybillBo, Waybill.WaybillSource.JUMA_CLIENT,
                loginEmployee);

        // 操作轨迹--建单
        waybillOperateTrackService.insert(id, OperateType.CREATE_WAYBILL,
                OperateApplication.CUSTOMER_SYS,
                buildTrackNotRequieParam(waybillBo.getWaybill(), ("首次派车方式:" + waybillBo.getWaybill().getReceiveWay())),
                loginEmployee);

        // 操作轨迹--指派司机
        waybillOperateTrackService.insert(id, OperateType.ASSIGNED_SYS,
                OperateApplication.CUSTOMER_SYS, buildTrackNotRequieParam(waybill, null),
                loginEmployee);

        return id;
    }

    /**
     * 客户经理-修改计划用车时间
     *
     * @param waybill
     * @param loginEmployee
     */
    @RequestMapping(value = "modifyPlanDeliveryTime", method = RequestMethod.POST)
    @ResponseBody
    public void modifyPlanDeliveryTime(@RequestBody Waybill waybill, LoginEmployee loginEmployee) {
        waybillService.modifyPlanDeliveryTimeByManager(waybill, loginEmployee);
        // 操作轨迹
        Waybill wb = waybillService.getWaybill(waybill.getWaybillId());
        String remark = null;
        if (null != wb && null != wb.getPlanDeliveryTime()) {
            remark = "原计划用车时间：" + DateUtil.format(wb.getPlanDeliveryTime());
        }
        waybillOperateTrackService.insert(waybill.getWaybillId(),
                OperateType.MODIFY_PLAN_DELIVERY_TIME,
                OperateApplication.CUSTOMER_SYS, buildTrackNotRequieParam(waybill, remark),
                loginEmployee);
    }

    /**
     * 司机端-修改计划用车时间
     *
     * @param waybill
     * @param driverLoginEcoUser
     */
    @RequestMapping(value = "driverModifyPlanDeliveryTime", method = RequestMethod.POST)
    @ResponseBody
    public void driverModifyPlanDeliveryTime(@RequestBody Waybill waybill, LoginEcoUser driverLoginEcoUser) {
        waybillService.modifyPlanDeliveryTimeBydriver(waybill, driverLoginEcoUser);
        // 操作轨迹
        waybillOperateTrackService.insert(waybill.getWaybillId(),
                OperateType.MODIFY_PLAN_DELIVERY_TIME,
                OperateApplication.DRIVER_SYS, null, driverLoginEcoUser);
    }

    /**
     * 取消运单
     */
    @ResponseBody
    @RequestMapping(value = "manageApp/cancelWaybill", method = RequestMethod.POST)
    public void cancelWaybillForApp(@RequestBody Waybill waybill, LoginEmployee loginEmployee) {
        Waybill wb = waybillCommonService.getWaybillById(waybill.getWaybillId());
        if (null == wb) {
            return;
        }

        // 承运商不能取消承运运单
        if (NumberUtils.compare(Waybill.WaybillSource.TRANSFORM_BILL.getCode(), wb.getWaybillSource()) == 0) {
            throw new BusinessException("transformBillCannotCancel", "waybill.error.transformBillCannotCancel");
        }

        waybillService.cancelWaybill(waybill.getWaybillId(), Waybill.CancelChannel.JUMA_CLIENT,
                waybill.getWaybillCancelRemark(), loginEmployee);

        // 操作轨迹
        waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.CANCEL,
                OperateApplication.CUSTOMER_SYS, buildTrackNotRequieParam(waybill, null),
                loginEmployee);
    }

    /**
     * 增加运费
     */
    @RequestMapping(value = "addPriceWaybill", method = RequestMethod.GET)
    @ResponseBody
    public void addPriceWaybill(Integer waybillId, Integer addPrice, LoginEmployee loginEmployee) {
        Waybill waybillDb = waybillService.getWaybill(waybillId);
        if (waybillDb == null) return;

        Waybill waybill = new Waybill();
        waybill.setWaybillId(waybillId);
        waybill.setEstimateFreight(waybill.getEstimateFreight().add(new BigDecimal(addPrice)));

        waybillService.addPriceWaybill(waybillId, addPrice, loginEmployee);

        // 操作轨迹
        String remark = "原件" + waybillDb.getEstimateFreight() + "，更换成" + addPrice;
        waybillOperateTrackService.insert(waybillId, OperateType.UPDATE_FREIGHT,
                OperateApplication.CUSTOMER_SYS, buildTrackNotRequieParam(waybill, remark),
                loginEmployee);
    }

    /**
     * 得到当前用户未完成运单数
     */
    @RequestMapping(value = "getWaybillStatusNotEnd", method = RequestMethod.GET)
    @ResponseBody
    public Integer getWaybillStatusNotEnd(LoginEmployee loginEmployee) {
        return waybillService.getWaybillStatusNotEnd(loginEmployee);
    }

    // FIXME 专车运单列表适配

    @ApiOperation(value = "经纪人端运单列表查询", notes = "收款状态:NOT_COLLECTION(1, \"未收款\"), SEGMENT_COLLECTION(2, \"部分收款\"), HAS_COLLECTION(3, \"已收款\");\n "
            + "结算状态:NOT_CLEAR(0, \"未结算\"), PREPARE_CLEAR(2, \"预结算\"), HAS_CLEAR(1, \"已结算\");\n "
            + "对账状态:NOT_RECONCILIATION(1, \"未对账\"), HAS_RECONCILIATION(2, \"已对账\"), IN_THE_ACCOUNT(4, \"对账中\");\n"
            + "TEMP(-2, \"非用户可见状态\"),\n" + "HAS_TIMED_OUT(-1, \"已超时\"),\n" + "DEFAULT(0, \"异常订单\"),\n"
            + "WATING_RECEIVE(1, \"派车中\"),\n" + "WATING_DELIVERY(2, \"待配送\"),\n" + "DELIVERYING(3, \"配送中\"),\n"
            + "WATING_PAY(4, \"待支付\"),\n" + "FINISH(5, \"已完成\"),\n" + "CANCEL(6, \"已取消\")")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filters.statusViewList", value = "配送状态", required = false, dataType = "array", paramType = "body"),
            @ApiImplicitParam(name = "filters.settlementStatusList", value = "结算状态", required = false, dataType = "array", paramType = "body"),
            @ApiImplicitParam(name = "filters.receiptStatusList", value = "收款状态", required = false, dataType = "array", paramType = "body"),
            @ApiImplicitParam(name = "filters.reconciliationStatusList", value = "对账状态", required = false, dataType = "array", paramType = "body"),
            @ApiImplicitParam(name = "filters.startTime", value = "用车时间开始", required = false, dataType = "string", paramType = "string", example = "yyyy-MM-dd HH:mm:ss"),
            @ApiImplicitParam(name = "filters.endTime", value = "用车时间结束", required = false, dataType = "string", paramType = "string", example = "yyyy-MM-dd HH:mm:ss"),
            @ApiImplicitParam(name = "filters.additionalFunctionIds", value = "用车要求", required = false, dataType = "string", paramType = "string", example = "11,12"),
            @ApiImplicitParam(name = "filters.type", value = "type", required = false, dataType = "string", paramType = "string"),
            @ApiImplicitParam(name = "filters.customerId", value = "customerId", required = false, dataType = "string", paramType = "string") })

    /**
     * 分页查询运单列表(货主端，经纪人端)
     */
    @RequestMapping(value = "selectPageWaybill", method = RequestMethod.POST)
    @ResponseBody
    public Page<WaybillDetailInfo> selectPageWaybill(LoginEmployee loginEmployee,
            @RequestBody PageCondition pageCondition) {
        Map<String, Object> filters = pageCondition.getFilters();
        if (null == filters) {
            filters = new HashMap<>();
        }
        filters.put("customerManagerId", loginEmployee.getEmployeeId());
        filters.put("tenantId", loginEmployee.getTenantId());
        filters.put("createUserId", loginEmployee.getUserId());//查询当前登录人等于创建人
        filters.put("projectManagerUserId", loginEmployee.getUserId());//查询当前登录人等于客户经理
        pageCondition.setFilters(filters);

        this.buildQueryParam(pageCondition);

        // 有参数但在转换过程中没有相应数据则返回空
        Map<String, Object> param = pageCondition.getFilters();
        Boolean hasParam = (Boolean) param.get("_hasParams");
        if (hasParam && param.get("customerId") == null && param.get("truckCustomerId") == null
                && param.get("projectId") == null) {
            return new Page<WaybillDetailInfo>(pageCondition.getPageNo(), pageCondition.getPageSize());
        }

        return waybillService.searchPageList(pageCondition, loginEmployee);
    }

    /**
     * 根据运单ID得到运单详情(客户经理端：驹马员工使用)
     */
    @RequestMapping(value = "manager/{waybillId}/getWaybillInfo", method = RequestMethod.GET)
    @ResponseBody
    public WaybillDetailInfo managerGetWaybillById(@PathVariable Integer waybillId, LoginEmployee loginEmployee) {
        WaybillDetailInfo result = waybillService.getWaybillInfo(waybillId, loginEmployee);
        this.buildReceiptManageInfo(result);
        this.buildReportInfo(result, loginEmployee);
        return result;
    }

    /**
     * 组装报备信息
     *
     * @param result
     */
    private void buildReportInfo(WaybillDetailInfo result, LoginUser loginUser) {
        List<ReportInfoDetails> data = reportInfoService
                .listAllReportInfoByWaybillId(result.getWaybill().getWaybillId(), loginUser);

        result.setReportInfoList(data);
    }

    /**
     * 组装回单数据
     *
     * @param result
     */
    private void buildReceiptManageInfo(WaybillDetailInfo result) {
        // 后台确认后参能展示回单
        if (NumberUtils.compare(result.getWaybill().getNeedReceipt(),
                Waybill.NeedReceipt.HAS_NEED_RECEIPT.getCode()) != 0)
            return;

        List<ImageUploadManage> data = receiptManageService
                .listReceiptImageByWaybillId(result.getWaybill().getWaybillId());
        result.setReceiptManageList(data);
    }

    /**
     * 根据运单ID得到运单详情(货主端：驹马生态用户使用)
     */
    @RequestMapping(value = "truckCustomer/{waybillId}/getWaybillInfo", method = RequestMethod.GET)
    @ResponseBody
    public WaybillDetailInfo customerGetWaybillById(@PathVariable Integer waybillId,
            LoginEcoUser cargoOwnerLoginEcoUser) {
        return waybillService.getWaybillInfo(waybillId, cargoOwnerLoginEcoUser);
    }

    /**
     * 根据运单ID得到运单详情(司机端：驹马生态用户使用)
     */
    @RequestMapping(value = "driver/{waybillId}/getWaybillInfo", method = RequestMethod.GET)
    @ResponseBody
    public WaybillDetailInfo driverGetWaybillById(@PathVariable Integer waybillId, LoginEcoUser driverLoginEcoUser) {
        // 司机已阅读
        waybillParamService.driverReadWaybill(waybillId, driverLoginEcoUser);
        WaybillDetailInfo detail =  waybillService.getWaybillInfo(waybillId, driverLoginEcoUser);
        //收货地排序 sequence
        Collections.sort(detail.getWaybillReceiveAddresses(), new Comparator<WaybillReceiveAddress>() {
            @Override
            public int compare(WaybillReceiveAddress o1, WaybillReceiveAddress o2) {
                if(o1.getSequence() == null || o2.getSequence() == null) return 1;
                return o1.getSequence().compareTo(o2.getSequence());
            }
        });
        
        return detail;
    }

    /**
     * 人工指派
     */
    @RequestMapping(value = "assignCar", method = RequestMethod.POST)
    @ResponseBody
    public void assignCar(@RequestBody List<Waybill> waybills, LoginEmployee loginEmployee) {

        if (CollectionUtils.isEmpty(waybills)) {
            throw new BusinessException("waybillBoNull", "errors.paramError");
        }
        for (Waybill waybill : waybills) {
            waybillService.changeNewToManual(waybill.getWaybillId(), loginEmployee);

            // 操作轨迹
            waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.MANUAL_ASSIGN,
                    OperateApplication.CUSTOMER_SYS, buildTrackNotRequieParam(waybill, null),
                    loginEmployee);
        }
    }

    /**
     * 指定车辆
     */
    // @RequestMapping(value = "appointCar", method = RequestMethod.POST)
    // @ResponseBody
    // public void appointCar(@RequestBody Waybill waybill, LoginEmployee loginEmployee) {
    // waybill.setReceiveWay(Waybill.ReceiveWay.ASSIGNED.getCode());
    // Waybill wb = waybillService.getWaybill(waybill.getWaybillId());
    // if (null == wb) {
    // throw new BusinessException("waybillNotfound", "waybill.error.notfound");
    // }

    // Driver driver = driverService.getDriver(waybill.getDriverId());
    // if (null == driver) {
    // throw new BusinessException("driverNotFound", "driver.error.not.found");
    // }
    //
    // Integer flightId = tmsFlightUsageService.findFlightIdBy(wb.getPlanDeliveryTime(),
    // wb.getEstimateTimeConsumption(), driver.getAmsDriverId(), loginEmployee);
    // waybill.setFlightId(flightId);
    // waybillService.changeToAssigned(waybill.getWaybillId(), waybill.getDriverId(), waybill.getTruckId(), flightId,
    // Waybill.ReceiveWay.ASSIGNED.getCode(), null, loginEmployee);
    //
    // // 操作轨迹
    // waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.ASSIGNED_SYS,
    // OperateApplication.CUSTOMER_SYS, buildTrackNotRequieParam(waybill, null),
    // loginEmployee);
    // }

    /**
     * 多单同时指派
     *
     * @param waybills
     * @param loginEmployee
     */
    @RequestMapping(value = "appointCarBatch", method = RequestMethod.POST)
    @ResponseBody
    public void appointCar(@RequestBody List<Waybill> waybills, LoginEmployee loginEmployee) {
        if (CollectionUtils.isEmpty(waybills)) return;

        waybillService.changeToAssignedBatch(waybills, loginEmployee);
    }

    /**
     * 指定运单为分享运单
     *
     * @param waybill
     * @param loginEmployee
     */
    @RequestMapping(value = "shareBill", method = RequestMethod.POST)
    @ResponseBody
    public void shareBill(@RequestBody Waybill waybill, LoginEmployee loginEmployee) {
        // Waybill wb = waybillService.getWaybill(waybill.getWaybillId());
        // if (null == wb) {
        // throw new BusinessException("waybillNotfound", "waybill.error.notfound");
        // }
        // waybillService.changeToShareBill(waybill, loginEmployee);
        // // 操作轨迹
        // waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.SHARE_BILL,
        // OperateApplication.CUSTOMER_SYS, buildTrackNotRequieParam(waybill, null),
        // loginEmployee);
    }

    /**
     * 系统派车
     */
    @RequestMapping(value = "sysAssignCar", method = RequestMethod.POST)
    @ResponseBody
    public void sysAssignCar(@RequestBody List<Waybill> waybills, LoginEmployee loginEmployee) {
        if (CollectionUtils.isEmpty(waybills)) {
            throw new BusinessException("waybillNull", "errors.paramError");
        }
        for (Waybill waybill : waybills) {
            waybillService.changeToWaitingReceive(waybill.getWaybillId(), loginEmployee);

            // 操作轨迹
            waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.ASSIGNED,
                    OperateApplication.CUSTOMER_SYS, buildTrackNotRequieParam(waybill, null),
                    loginEmployee);
        }
    }

    /**
     * 可分享业务区域
     *
     * @param areaCode
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "{areaCode}/shareArea", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getShareAreaData(@PathVariable(value = "areaCode") String areaCode,
            LoginEmployee loginEmployee) {
        Map<String, Object> rst = new HashMap<>();

        // 所有可分享业务区域
        List<BusinessAreaNode> treeData = tgmBusinessAreaService.getLogicBusinessAreaTree(loginEmployee);
        rst.put("shareArea", treeData);

        // 当前业务区域
        BusinessArea ba = businessAreaService.findBusinessArea(areaCode, loginEmployee);
        if (ba == null) return rst;

        // 获取所有父级节点
        List<BusinessArea> parentNode = tgmBusinessAreaService.findAllParentNode(ba.getBusinessAreaId(), loginEmployee);
        Collections.reverse(parentNode);
        String allName = "";
        for (BusinessArea pn : parentNode) {
            allName += pn.getAreaName() + ",";
        }
        String newName = StringUtils.substringBeforeLast(allName, ",");
        ba.setAreaName(newName);
        rst.put("currentArea", ba);
        return rst;
    }

    /**
     * 得到最近使用的5个源地址 经纪人
     */
    @RequestMapping(value = "getWaybillDeliveryLastAddress", method = RequestMethod.POST)
    @ResponseBody
    public List<AddressHistory> getWaybillDeliveryLastAddress(@RequestBody PageCondition pageCondition,
            LoginEmployee loginEmployee) {
        // PageCondition pageCondition = new PageCondition();
        return doGetAddressHistories(pageCondition, loginEmployee, AddressType.START.getCode());
    }

    /**
     * 得到最近使用的5个源地址 微信货主
     */
    @RequestMapping(value = "getWaybillDeliveryLastAddressForWx", method = RequestMethod.POST)
    @ResponseBody
    public List<AddressHistory> getWaybillDeliveryLastAddressForWx(LoginEcoUser cargoOwnerLoginEcoUser) {
        PageCondition pageCondition = new PageCondition();
        return doGetAddressHistories(pageCondition, cargoOwnerLoginEcoUser, AddressType.START.getCode());
    }

    private List<AddressHistory> doGetAddressHistories(@RequestBody PageCondition pageCondition, LoginUser loginUser,
            int code) {
        pageCondition.setPageNo(1);
        if (pageCondition.getPageSize() == null || pageCondition.getPageSize() <= 0) {
            pageCondition.setPageSize(20);
        }
        pageCondition.getFilters().put("addressType", code);
        return addressHistoryService.getAddressAndContactHistory(pageCondition, loginUser);
    }

    /**
     * 得到最近使用的5个目的地址 经纪人
     */
    @RequestMapping(value = "getWaybillReceiveLastAddress", method = RequestMethod.POST)
    @ResponseBody
    public List<AddressHistory> getWaybillReceiveLastAddress(LoginEmployee loginEmployee,
            @RequestBody PageCondition pageCondition) {
        // PageCondition pageCondition = new PageCondition();
        return doGetAddressHistories(pageCondition, loginEmployee, AddressType.END.getCode());
    }

    /**
     * 得到最近使用的5个目的地址 微信货主
     */
    @RequestMapping(value = "getWaybillReceiveLastAddressForWx", method = RequestMethod.POST)
    @ResponseBody
    public List<AddressHistory> getWaybillReceiveLastAddressForWx(LoginEcoUser cargoOwnerLoginEcoUser) {
        PageCondition pageCondition = new PageCondition();
        return doGetAddressHistories(pageCondition, cargoOwnerLoginEcoUser, AddressType.END.getCode());
    }

    /**
     * 得到运单的状态
     */
    @RequestMapping(value = "getWaybillStatus", method = RequestMethod.GET)
    @ResponseBody
    public Integer getWaybillStatus(Integer waybillId) {
        return waybillService.getWaybill(waybillId).getStatus();
    }

    /**
     * 运单轮询查询（客户经理端使用）
     */
    @RequestMapping(value = "manager/polling/query", method = RequestMethod.GET)
    @ResponseBody
    public WaybillPollingResponse managerPollingQuery(Integer waybillId) {
        WaybillPollingResponse response = new WaybillPollingResponse();
        Waybill waybill = waybillService.getWaybill(waybillId);
        if (null == waybill) return response;

        response.setStatusView(waybill.getStatusView());
        response.setAssignCarFeedback(waybill.getAssignCarFeedback());
        response.setUpdateFreightAuditStatus(waybill.getUpdateFreightAuditStatus());

        return response;
    }

    /**
     * 运单轮询查询（货主端使用）
     */
    @RequestMapping(value = "customer/polling/query", method = RequestMethod.GET)
    @ResponseBody
    public WaybillPollingResponse customerPollingQuery(Integer waybillId) {
        WaybillPollingResponse response = new WaybillPollingResponse();
        Waybill waybill = waybillService.getWaybill(waybillId);
        if (null != waybill) {
            response.setStatusView(waybill.getStatusView());
            response.setAssignCarFeedback(waybill.getAssignCarFeedback());
        }
        return response;
    }

    /**
     * 得到运单的显示状态(版本3.0.0之后查询微信不使用后删除)
     */
    @Deprecated
    @RequestMapping(value = "getWaybillStatusView", method = RequestMethod.GET)
    @ResponseBody
    public Integer getWaybillStatusView(Integer waybillId) {
        return waybillService.getWaybill(waybillId).getStatusView();
    }

    /**
     * 运单的状态列表
     */
    @RequestMapping(value = "status/list", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<Integer, Object>> getWaybillStatusList() {
        return waybillService.getWaybillStatus();
    }

    /**
     * 更改运单支付状态
     */
    @RequestMapping(value = "updatePayWaybillStatus", method = RequestMethod.GET)
    @ResponseBody
    public void updatePayWaybillStatus(String waybillId, Integer status, LoginEmployee loginEmployee) {
        waybillService.updatePayWaybillStatus(waybillId, status, loginEmployee);
    }

    /**
     * 判断是否有未完成的订单
     *
     * @param loginEmployee
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "judgeHasNewWaybill", method = RequestMethod.GET)
    public Waybill judgeHasNewWaybill(LoginEmployee loginEmployee) {
        return waybillService.findWaybillByOwnerUser(loginEmployee);
    }

    /**
     * 货主端改税前费用
     */
    @ResponseBody
    @RequestMapping(value = "updateFreight", method = RequestMethod.POST)
    public Waybill updateFreight(@RequestBody Waybill waybill, LoginEmployee loginEmployee) {
        Waybill origin = waybillService.getWaybill(waybill.getWaybillId());
        if (origin == null) return null;

        Waybill wb = waybillService.customerManagerModifyFreight(waybill, loginEmployee);

        if (wb != null) {
            // 操作轨迹
            String remark = "原件" + origin.getEstimateFreight() + "，更换成" + waybill.getEstimateFreight();
            waybillOperateTrackService.insert(origin.getWaybillId(), OperateType.UPDATE_FREIGHT,
                    OperateApplication.CUSTOMER_SYS, buildTrackNotRequieParam(origin, remark),
                    loginEmployee);
        }

        return wb;
    }

    /**
     * 客户经理修改司机结算价
     *
     * @param waybill
     * @param loginEmployee
     */
    @ResponseBody
    @RequestMapping(value = "updateDriverFreight", method = RequestMethod.POST)
    public void updateShow4DriverFreight(@RequestBody Waybill waybill, LoginEmployee loginEmployee) {

        Waybill origin = waybillService.getWaybill(waybill.getWaybillId());

        waybillService.customerManagerModifyShow4DriverFreight(waybill, loginEmployee);

        // 操作轨迹
        String remark = "原件" + origin.getShow4DriverFreight() + "，更换成" + waybill.getShow4DriverFreight();
        waybillOperateTrackService.insert(origin.getWaybillId(),
                OperateType.UPDATE_SHOW_4_DRIVER_FREIGHT,
                OperateApplication.CUSTOMER_SYS, buildTrackNotRequieParam(origin, remark),
                loginEmployee);

    }

    @RequestMapping(value = "aroundLocation", method = RequestMethod.POST)
    public List<DriverLocation> aroundLocation(String location) {
        return gaoDeMapService.searchLocation("成都", location);
    }

    /**
     * 落地配-运单跟踪
     *
     * @param waybillId
     * @return
     */
    @ResponseBody
    @RequestMapping("scattered/{waybillId}/track")
    public ScatteredWaybillTrackDetailBo scatteredWaybillTrack(@PathVariable("waybillId") Integer waybillId) {
        // 获取运单详情
        Waybill waybill = waybillService.getWaybill(waybillId);
        // 组装数据
        ScatteredWaybillTrackDetailBo waybillTrackDetailBo = null;
        if (NumberUtils.compare(waybill.getStatus(), Waybill.Status.WATING_RECEIVE.getCode()) == 0
                || NumberUtils.compare(waybill.getStatus(), Waybill.Status.NO_DRIVER_ANSWER.getCode()) == 0) {// 派车中状态只展示下单成功
            waybillTrackDetailBo = scatteredWaybillTrackDetailBuilder.buildTrackDetailBo(waybill,
                    Arrays.asList(new Integer[] { OperateType.CREATE_WAYBILL.getCode() }), 0);
        } else {
            waybillTrackDetailBo = scatteredWaybillTrackDetailBuilder.buildTrackDetailBo(waybill,
                    ScatteredWaybillTrackDetailBuilder.allOperationList, 0);
        }

        return waybillTrackDetailBo;
    }

    /**
     * 客户经理接口 落地配-确认收到运费
     *
     * @param waybillId
     * @param loginEmployee
     */
    @ResponseBody
    @RequestMapping(value = "scattered/{waybillId}/confirmFreight", method = RequestMethod.POST)
    public void confirmFreightAccept(@PathVariable("waybillId") Integer waybillId, LoginEmployee loginEmployee) {
        scatteredWaybillService.confirmFreightAccept(waybillId, loginEmployee);
        // 操作轨迹
        waybillOperateTrackService.insert(waybillId, OperateType.PAIED,
                OperateApplication.CUSTOMER_SYS, null, loginEmployee);
    }

    /**
     * 客户经理接口 落地配-确认代收货款已
     *
     * @param waybillId
     * @param loginEmployee
     */
    @ResponseBody
    @RequestMapping(value = "scattered/{waybillId}/confirmFeeDelivery", method = RequestMethod.POST)
    public void confirmFeeDelivery(@PathVariable("waybillId") Integer waybillId, LoginEmployee loginEmployee) {
        scatteredWaybillService.confirmFeeDelivery(waybillId, loginEmployee);
    }

    /**
     * 经济人端接口 落地配-取消运单
     *
     * @param waybill
     * @param loginEmployee
     */
    @ResponseBody
    @RequestMapping(value = "scattered/cancelWaybill", method = RequestMethod.POST)
    public void customerManagerCancelScatteredBill(@RequestBody Waybill waybill, LoginEmployee loginEmployee) {
        Waybill wb = waybillCommonService.getWaybillById(waybill.getWaybillId());
        if (null == wb) {
            return;
        }

        // 承运商不能取消承运运单
        if (NumberUtils.compare(Waybill.WaybillSource.TRANSFORM_BILL.getCode(), wb.getWaybillSource()) == 0) {
            throw new BusinessException("transformBillCannotCancel", "waybill.error.transformBillCannotCancel");
        }

        scatteredWaybillService.customerManagerCancelBill(waybill, loginEmployee);

        // 操作轨迹
        waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.CANCEL,
                OperateApplication.CUSTOMER_SYS, buildTrackNotRequieParam(waybill, null),
                loginEmployee);

    }

    /**
     * 客户经理 新建落地配运单
     *
     * @param scatteredWaybillCreateVo
     * @param loginEmployee
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "scattered/createWaybill", method = RequestMethod.POST)
    public List<Integer> createScatteredWaybill(@RequestBody ScatteredWaybillCreateVo scatteredWaybillCreateVo,
            LoginEmployee loginEmployee) {
        Waybill waybill = scatteredWaybillCreateVo.getWaybill();
        if (waybill == null) throw new BusinessException("waybillNullError", "errors.paramCanNotNullWithName", "运单参数");
        waybill.setCustomerManagerId(loginEmployee.getEmployeeId());
        waybill.setWaybillSource(Waybill.WaybillSource.JUMA_CLIENT.getCode());

        List<Integer> ids = scatteredWaybillService.createScatteredWaybillForCustomerManager(scatteredWaybillCreateVo,
                loginEmployee);

        // 操作轨迹--建单
        for (Integer id : ids) {
            waybillOperateTrackService.insert(id, OperateType.CREATE_WAYBILL,
                    OperateApplication.CUSTOMER_SYS,
                    buildTrackNotRequieParam(scatteredWaybillCreateVo.getWaybill(),
                            ("首次派车方式:" + Waybill.ReceiveWay.AUTO_ASSIGN)),
                    loginEmployee);
        }

        return ids;
    }

    private void isAtFenceArea(ScatteredWaybillCreateVo scatteredWaybillCreateVo, LoginEmployee loginEmployee) {
        CityAdressData srcAddress = new CityAdressData();
        List<CityAdressData> toAddress = new ArrayList<CityAdressData>();
        if (scatteredWaybillCreateVo.getSrcAddress() == null || scatteredWaybillCreateVo.getSrcAddress().isEmpty()
                || scatteredWaybillCreateVo.getDestAddress() == null
                || scatteredWaybillCreateVo.getDestAddress().isEmpty())
            throw new BusinessException("waybillNullError", "errors.paramCanNotNullWithName", "运单地址参数");
        WaybillDeliveryAddress deliveryAddress = scatteredWaybillCreateVo.getSrcAddress().get(0);
        srcAddress.setCity(deliveryAddress.getAddressName());
        srcAddress.setAddress(deliveryAddress.getAddressName());
        srcAddress.setAddressDetail(deliveryAddress.getAddressDetail());
        srcAddress.setRegionCode(deliveryAddress.getRegionCode());
        srcAddress.setCoordinate(deliveryAddress.getCoordinates());
        for (WaybillReceiveAddress destAddress : scatteredWaybillCreateVo.getDestAddress()) {
            CityAdressData toCityAddress = new CityAdressData();
            toCityAddress.setCity(destAddress.getAddressName());
            toCityAddress.setAddress(destAddress.getAddressName());
            toCityAddress.setAddressDetail(destAddress.getAddressDetail());
            toCityAddress.setRegionCode(destAddress.getRegionCode());
            toCityAddress.setCoordinate(destAddress.getCoordinates());
            toAddress.add(toCityAddress);
        }
        scatteredWaybillService.isAtFenceArea(srcAddress, toAddress, loginEmployee);
    }

    /**
     * 落地配-价格-距离-是否入城-是否在业务区域 货主端
     *
     * @param dp
     * @param cargoOwnerLoginEcoUser
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "scattered/cargoOwner/getDistanceAndPrice", method = RequestMethod.POST)
    public DistanceAndPriceData getScatteredDistanceAndPriceForCargoOwner(@RequestBody DistanceAndPriceParamVo dp,
            LoginEcoUser cargoOwnerLoginEcoUser) {
        return this.doGetDistanceAndPriceData(dp, cargoOwnerLoginEcoUser);
    }

    /**
     * 落地配-价格-距离-是否入城-是否在业务区域 客户经理端
     *
     * @param dp
     * @param loginEmployee
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "scattered/manager/getDistanceAndPrice", method = RequestMethod.POST)
    public DistanceAndPriceData getScatteredDistanceAndPriceForManager(@RequestBody DistanceAndPriceParamVo dp,
            LoginEmployee loginEmployee) {
        return this.doGetDistanceAndPriceData(dp, loginEmployee);
    }

    /**
     * 落地配-价格-距离-是否入城-是否在业务区域
     *
     * @param dp
     * @param loginUser
     * @return
     */
    private DistanceAndPriceData doGetDistanceAndPriceData(@RequestBody DistanceAndPriceParamVo dp,
            LoginUser loginUser) {
        // waybillCommonServiceAsync.getGaodeMapInfo(dp.getSrcAddress(), dp.getToAddress());
        // Future<DistanceAndPriceData> distanceFuture = RpcContext.getContext().getFuture();
        // scatteredWaybillServiceAsync.isAtFenceArea(dp.getSrcAddress(), dp.getToAddress(), loginUser);
        // Future<AtFenceResultVo> fenceFuture = RpcContext.getContext().getFuture();
        //
        // AtFenceResultVo atFenceResult = null;
        // DistanceAndPriceData distanceData = null;
        // try {
        // long distanceTimeStart = System.currentTimeMillis();
        // distanceData = distanceFuture.get();
        // long distanceTimeEnd = System.currentTimeMillis();
        // atFenceResult = fenceFuture.get();
        // long fenceTimeEnd = System.currentTimeMillis();
        // log.debug("distanceTime:" + (distanceTimeEnd - distanceTimeStart));
        // log.debug("fenceTime:" + (fenceTimeEnd - distanceTimeEnd));
        // } catch (InterruptedException e) {
        // log.error("异步接口调用失败", e);
        // } catch (ExecutionException e) {
        // log.error("异步接口调用失败", e);
        // }
        DistanceAndPriceData rst = new DistanceAndPriceData();
        // 电子围栏判断
        AtFenceResultVo atFenceResult = scatteredWaybillService.isAtFenceArea(dp.getSrcAddress(), dp.getToAddress(),
                loginUser);

        // 预估距离判断
        this.buildRegionData(dp, rst, loginUser);

        boolean inCity = false;
        if (atFenceResult != null) {
            rst.setInBusinessArea(atFenceResult.getAtBusinessArea());
            rst.setInCity(atFenceResult.getAtCity());
            rst.setInForbiddenArea(atFenceResult.isAtForbiddenArea());
            rst.setForbiddenType(atFenceResult.getForBiddenType());
            rst.setForbiddenAreaIndex(atFenceResult.getForbiddenAreaIndex());
            inCity = atFenceResult.getAtCity();
        }

        Waybill waybill = dp.getWaybill();
        if (waybill == null) return rst;
        waybill.setEstimateDistance(rst.getDistance());
        waybill.setTolls(rst.getTolls());
        if (rst.getDistance() == null) return rst;
        if (dp.getTruckRequire() == null) return rst;
        TruckRequire truckRequire = dp.getTruckRequire();
        this.addEntryLicenseFunction(inCity, truckRequire);
        // if (truckRequire.getGoodsVolume() == null)
        // return rst;
        // if (truckRequire.getGoodsWeight() == null)
        // return rst;
        waybill.setRegionCode(rst.getRegionCode());
        if (StringUtils.isBlank(waybill.getRegionCode())) return rst;

        // 价格计算
        BigDecimal price = scatteredWaybillService.computeFreight(dp, loginUser);
        if (price != null) {
            rst.setPrice(this.calculateWithTaxPrice(price, truckRequire.getTaxRateValue()));
        }
        return rst;
    }

    /**
     * 希地计算含税价格
     *
     * @param purePrice
     * @param taxRate
     * @return
     */
    private BigDecimal calculateWithTaxPrice(BigDecimal purePrice, BigDecimal taxRate) {
        if (purePrice == null) return null;
        if (taxRate == null) return purePrice;

        BigDecimal withTaxPrice = purePrice.multiply(BigDecimal.ONE.add(taxRate));

        return withTaxPrice;
    }

    /**
     * 用车要求添加入城证
     *
     * @param inCity
     * @param truckRequire
     */
    private void addEntryLicenseFunction(boolean inCity, TruckRequire truckRequire) {
        if (!inCity) return;

        if (truckRequire == null) return;
        AdditionalFunction function = additionalFunctionService
                .findAdditionFunctionByKey(AdditionalFunction.FunctionKeys.ENTRY_LICENSE.name());

        if (function == null) {
            log.error("找不到入城证配置:{}", new String[] { AdditionalFunction.FunctionKeys.ENTRY_LICENSE.name() });
            return;
        }

        // 没有填用车要求
        if (StringUtils.isBlank(truckRequire.getAdditionalFunctionIds())) {
            truckRequire.setAdditionalFunctionIds(function.getAdditionalFunctionId() + "");
            return;
        }

        // 填写了用车要求
        // 是否有入城证id
        String[] ids = StringUtils.split(truckRequire.getAdditionalFunctionIds(), ",");

        Arrays.sort(ids);
        if (Arrays.binarySearch(ids, function.getAdditionalFunctionId() + "") > 0) return;

        String[] nIds = (String[]) ArrayUtils.add(ids, function.getAdditionalFunctionId() + "");

        StringBuffer sb = new StringBuffer();
        for (String id : nIds) {
            sb.append(id);
            sb.append(",");
        }
        String target = StringUtils.removeEnd(sb.toString(), ",");
        truckRequire.setAdditionalFunctionIds(target);
    }

    /**
     * 获取距离区域信息
     *
     * @param dp
     * @param rst
     * @param loginUser
     */
    private void buildRegionData(@RequestBody DistanceAndPriceParamVo dp, DistanceAndPriceData rst,
            LoginUser loginUser) {
        DistanceAndPriceData distanceData = waybillCommonService.getGaodeMapInfo(dp.getSrcAddress(), dp.getToAddress());

        if (distanceData == null) return;

        rst.setDistance(distanceData.getDistance());
        rst.setDuration(distanceData.getDuration());
        rst.setRegionCode(distanceData.getRegionCode());
        rst.setTollDistance(distanceData.getTollDistance());
        rst.setTolls(distanceData.getTolls());

        // 获取业务区域
        if (distanceData.getRegion() == null) return;

        Region region = distanceData.getRegion();
        rst.setRegionCode(StringUtils.left(region.getRegionCode(), 5));
        BusinessArea businessArea = businessAreaService.loadBelongingBusinessArea(loginUser.getTenantId(), region);
        distanceData.setRegion(null);
        if (businessArea == null) {
            // 找不到业务区域则将运单归属到总部
            businessArea = businessAreaService.loadLogicBusinessArea("00", loginUser);
        }

        if (businessArea == null) return;
        // 行政区域到市一级
        rst.setWaybillAreaCode(businessArea.getAreaCode());

    }

    /**
     * 出发城市列表
     */
    @ResponseBody
    @RequestMapping(value = "startCityList", method = RequestMethod.GET)
    public CityManageInfo startCityList() {
        return cityManageService.getCityList(CityManage.Sign.START_FROM.getCode());
    }

    /**
     * 到达城市列表
     */
    @ResponseBody
    @RequestMapping(value = "endCityList", method = RequestMethod.GET)
    public CityManageInfo endCityList() {
        return cityManageService.getCityList(CityManage.Sign.END_FORM.getCode());
    }

    /**
     * 获取今日运单
     */
    @ResponseBody
    @RequestMapping(value = "todayWaybillInfo", method = RequestMethod.GET)
    public WaybillInfo getTodayWaybillInfo(LoginEcoUser driverLoginEcoUser) {
        return waybillService.getWaybillInfo(driverLoginEcoUser);
    }

    /**
     * 获取昨日运单收入
     */
    @ResponseBody
    @RequestMapping(value = "yestodayIncome", method = RequestMethod.GET)
    public YesterdayIncomeInfo getYestodayIncome(DriverLoginUser driverLoginUser, LoginEcoUser driverLoginEcoUser) {
        return waybillReportService.getYestodayIncome(driverLoginUser);
    }

    /**
     * 超时运单被查看，更改运单置顶状态
     */
    @ResponseBody
    @RequestMapping(value = "updateOvertimeWaybill", method = RequestMethod.POST)
    public void updateOvertimeWaybill(@RequestBody Waybill waybill, CustomerLoginUser customerLoginUser,
            LoginEmployee loginEmployee) {
        waybillService.updateOvertimeWaybillHasClick(waybill, loginEmployee);
    }

    @ResponseBody
    @RequestMapping(value = "overtimeWaybillForCargoOwner", method = RequestMethod.POST)
    public void updateOvertimeWaybillForCargoOwner(@RequestBody Waybill waybill, LoginEcoUser cargoOwnerLoginEcoUser) {

        waybillService.updateOvertimeWaybillHasClick(waybill, cargoOwnerLoginEcoUser);

    }

    /**
     * 待配送运单列表(不止是今天的数据)
     */
    @Deprecated
    @RequestMapping(value = "todayList", method = RequestMethod.POST)
    @ResponseBody
    public Page<WaybillBo> todayList(LoginEcoUser driverLoginEcoUser, @RequestBody PageCondition pageCondition) {
        return waybillService.getTodayWaitList(pageCondition, driverLoginEcoUser);
    }

    /**
     * 查询未付款运单
     */
    @RequestMapping(value = "hasNoPayWaybill", method = RequestMethod.GET)
    @ResponseBody
    public WaybillInfo hasNoPayWaybill(LoginEmployee loginEmployee) {
        return waybillService.hasNoPayWaybill(loginEmployee);
    }

    /**
     * 计算司机点击到达目的地与取货地的距离
     */
    @RequestMapping(value = "confirmToDepot", method = RequestMethod.POST)
    @ResponseBody
    public WaybillInfo confirmToDepot(@RequestBody Waybill waybill, LoginEcoUser driverLoginEcoUser) {
        return waybillService.updateConfirmToDepot(waybill, driverLoginEcoUser);
    }

    /**
     * 完成配送 完善订单信息
     */
    @RequestMapping(value = "finish", method = RequestMethod.POST)
    @ResponseBody
    public void assignedWaybill(@RequestBody Waybill waybill, LoginEcoUser driverLoginEcoUser) {

        if (waybill.getWaybillId() == null) {
            throw new BusinessException("validationFailure", "errors.validation.failure");
        }

        Waybill waybillDb = waybillService.getWaybill(waybill.getWaybillId());
        if (waybillDb == null) {
            throw new BusinessException("waybillNotfound", "waybill.error.notfound");
        }

        checkWhoWriteWork(waybillDb);
        

        waybillService.changeToDeliveried(waybill, driverLoginEcoUser);

        // 操作轨迹
        waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.DELIVERYING,
                OperateApplication.DRIVER_SYS, buildTrackNotRequieParam(waybill, null),
                driverLoginEcoUser);

        // dubbo异步获取并记录里程信息
        waybillTrackService.syncChangeActualMileage(waybill.getWaybillId(), driverLoginEcoUser);
    }

    private void checkWhoWriteWork(Waybill waybill) {
        // 如果是经济人填写工作量&没有填写，通知司机信息
        WaybillParam waybillParam = null;
        if(waybill.getReceiveWay() == Waybill.ReceiveWay.TRANSFORM_BILL.getCode()) {
            waybillParam = waybillParamService.findByWaybillId(waybill.getWaybillId());
            waybillParam = waybillParamService.findByWaybillId(waybillParam.getTransformBillLinkId());
        } else {
            waybillParam = waybillParamService.findByWaybillId(waybill.getWaybillId());
        }
        if (waybillParam == null) return;
        String ruleJson = waybillParam.getProjectFreightRuleJson();
        if(StringUtils.isNotBlank(ruleJson)) {
            RoadMap roadMap = JSON.parseObject(ruleJson, RoadMap.class);
            if(roadMap != null 
                    && roadMap.getWhoWriteWork() != null
                    && roadMap.getWhoWriteWork().intValue() == 2) {
                //看下rule里面是不是只有起步价
                Map<String, String> ruleMap = JSON.parseObject(ruleJson, new TypeReference<HashMap<String, String>>() {});
                if (ruleMap.containsKey("factorJson")) {
                    ruleMap = JSON.parseObject(ruleMap.get("factorJson"), new TypeReference<HashMap<String, String>>() {});
                    //只有起步价
                    if (ruleMap.size() == 1 && ruleMap.containsKey("initiateRate")) {
                        
                    } else {
                        //如果工作量没有填
                        if(StringUtils.isBlank(waybillParam.getValuationConstJson())){
                            throw new BusinessException("noWriteWork", "请告知经纪人完善工作量后，才能结束配送");
                        }
                    }
                }
            }
        }
    }

    /**
     * 到达取货地
     */
    @RequestMapping(value = "arriveToDepot", method = RequestMethod.POST)
    @ResponseBody
    public void arriveToDepot(@RequestBody Waybill waybill, LoginEcoUser driverLoginEcoUser) {
        try {
            waybillService.updateArriveDepotTime(waybill, driverLoginEcoUser);
        } catch (BusinessException e) {
            if ("driverHasArriveDepot".equals(e.getErrorKey())) {
                // 操作轨迹
                waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.ARRIVE_DEPOT,
                        OperateApplication.DRIVER_SYS, buildTrackNotRequieParam(waybill, "电子围栏已到仓"),
                        driverLoginEcoUser);
                return;
            }
            throw e;
        }

        // 操作轨迹
        waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.ARRIVE_DEPOT,
                OperateApplication.DRIVER_SYS, buildTrackNotRequieParam(waybill, null),
                driverLoginEcoUser);
    }

    /**
     * 离开取货地=取货完成
     */
    @RequestMapping(value = "leaveDepot", method = RequestMethod.POST)
    @ResponseBody
    public void leaveDepot(@RequestBody Waybill waybill, LoginEcoUser driverLoginEcoUser) {
        try {
            waybillService.updateLeaveDepotTime(waybill, driverLoginEcoUser);
        } catch (BusinessException e) {
            if ("driverHasLeaveDepot".equals(e.getErrorKey())) {
                // 操作轨迹
                waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.LEAVE_DEPOT,
                        OperateApplication.DRIVER_SYS, buildTrackNotRequieParam(waybill, "电子围栏已离仓"),
                        driverLoginEcoUser);
                return;
            }
            throw e;
        }

        // 操作轨迹
        waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.LEAVE_DEPOT,
                OperateApplication.DRIVER_SYS, buildTrackNotRequieParam(waybill, null),
                driverLoginEcoUser);
    }

    // ==============统一APP ======================

    /**
     * 统一司机端 完成配送 完善订单信息
     */
    @RequestMapping(value = "v2/finish", method = RequestMethod.POST)
    @ResponseBody
    public void assignedWaybillV2(@RequestBody Waybill waybill, LoginEcoUser driverLoginEcoUser) {

        if (waybill.getWaybillId() == null) {
            throw new BusinessException("validationFailure", "errors.validation.failure");
        }

        Waybill waybillDb = waybillService.getWaybill(waybill.getWaybillId());
        if (waybillDb == null) {
            throw new BusinessException("waybillNotfound", "waybill.error.notfound");
        }

        driverLoginEcoUser.setTenantId(waybillDb.getTenantId());
        
        checkWhoWriteWork(waybillDb);
        
        waybillService.changeToDeliveried(waybill, driverLoginEcoUser);

        // 操作轨迹
        waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.DELIVERYING,
                OperateApplication.DRIVER_SYS, buildTrackNotRequieParam(waybill, null),
                driverLoginEcoUser);

        // dubbo异步获取并记录里程信息
        waybillTrackService.syncChangeActualMileage(waybill.getWaybillId(), driverLoginEcoUser);
    }

    /**
     * 统一司机端 到达取货地
     */
    @RequestMapping(value = "v2/arriveToDepot", method = RequestMethod.POST)
    @ResponseBody
    public void arriveToDepotV2(@RequestBody Waybill waybill, LoginEcoUser driverLoginEcoUser) {
        try {
            Waybill waybillDb = waybillService.getWaybill(waybill.getWaybillId());
            if (waybillDb == null) {
                throw new BusinessException("waybillNotfound", "waybill.error.notfound");
            }
            driverLoginEcoUser.setTenantId(waybillDb.getTenantId());
            waybillService.updateArriveDepotTime(waybill, driverLoginEcoUser);
            /*
             * if(waybillDb.getTenantId() == 3) { xidiWaybillService.updateArriveDepotTime(waybill, driverLoginEcoUser); } else { waybillService.updateArriveDepotTime(waybill,
             * driverLoginEcoUser); }
             */
        } catch (BusinessException e) {
            if ("driverHasArriveDepot".equals(e.getErrorKey())) {
                // 操作轨迹
                waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.ARRIVE_DEPOT,
                        OperateApplication.DRIVER_SYS, buildTrackNotRequieParam(waybill, "电子围栏已到仓"),
                        driverLoginEcoUser);
                return;
            }
            throw e;
        }

        // 操作轨迹
        waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.ARRIVE_DEPOT,
                OperateApplication.DRIVER_SYS, buildTrackNotRequieParam(waybill, null),
                driverLoginEcoUser);
    }

    /**
     * 统一司机端 离开取货地=取货完成
     */
    @RequestMapping(value = "v2/leaveDepot", method = RequestMethod.POST)
    @ResponseBody
    public void leaveDepotV2(@RequestBody Waybill waybill, LoginEcoUser driverLoginEcoUser) {
        try {
            Waybill waybillDb = waybillService.getWaybill(waybill.getWaybillId());
            if (waybillDb == null) {
                throw new BusinessException("waybillNotfound", "waybill.error.notfound");
            }
            driverLoginEcoUser.setTenantId(waybillDb.getTenantId());
            waybillService.updateLeaveDepotTime(waybill, driverLoginEcoUser);
            /*
             * if(waybillDb.getTenantId() == 3) { xidiWaybillService.updateLeaveDepotTime(waybill, driverLoginEcoUser); } else { waybillService.updateLeaveDepotTime(waybill,
             * driverLoginEcoUser); }
             */
        } catch (BusinessException e) {
            if ("driverHasLeaveDepot".equals(e.getErrorKey())) {
                // 操作轨迹
                waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.LEAVE_DEPOT,
                        OperateApplication.DRIVER_SYS, buildTrackNotRequieParam(waybill, "电子围栏已离仓"),
                        driverLoginEcoUser);
                return;
            }
            throw e;
        }

        // 操作轨迹
        waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.LEAVE_DEPOT,
                OperateApplication.DRIVER_SYS, buildTrackNotRequieParam(waybill, null),
                driverLoginEcoUser);
    }

    /**
     * 司机确认收款
     */
    @RequestMapping(value = "confirmReivedFreight", method = RequestMethod.POST)
    @ResponseBody
    public void confirmReivedFreight(@RequestBody Waybill waybill, LoginEcoUser driverLoginEcoUser) {
        waybillService.updateConfirmReceivedFreight(waybill, driverLoginEcoUser);

        // 操作轨迹
        waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.RECEIVED_FREIGHT,
                OperateApplication.DRIVER_SYS, buildTrackNotRequieParam(waybill, null),
                driverLoginEcoUser);
    }

    /**
     * 司机添加或更改配送点数
     */
    @RequestMapping(value = "addPoints", method = RequestMethod.POST)
    @ResponseBody
    public void addPoints(@RequestBody WaybillParam waybillParam, LoginEcoUser driverLoginEcoUser) {
        waybillParamService.addOrUpdateOnly(waybillParam, driverLoginEcoUser);
    }

    /**
     * 司机添加目的地
     */
    @RequestMapping(value = "add/receive/address", method = RequestMethod.POST)
    @ResponseBody
    public WaybillReceiveAddress addReceiveAddress(@RequestBody WaybillReceiveAddress waybillReceiveAddress,
            LoginEcoUser driverLoginEcoUser) {
        if (null == waybillReceiveAddress.getWaybillId()) {
            throw new BusinessException("waybillIdRequire", "waybillAddress.error.waybillIdRequire");
        }

        if (StringUtils.isBlank(waybillReceiveAddress.getAddressName())) {
            throw new BusinessException("addressNameRequire", "waybillAddress.error.addressNameRequire");
        }

        if (StringUtils.isBlank(waybillReceiveAddress.getCoordinates())) {
            throw new BusinessException("coordinatesRequire", "waybillAddress.error.coordinatesRequire");
        }

        if (StringUtils.isBlank(waybillReceiveAddress.getAddressDetail())) {
            throw new BusinessException("addressDetailRequire", "waybillAddress.error.addressDetailRequire");
        }

        if (StringUtils.isBlank(waybillReceiveAddress.getRegionCode())) {
            String regionCode = regionTgmService.findRegionCodeByCoordinate(waybillReceiveAddress.getCoordinates());
            if (regionCode.length() > 5) {
                regionCode = regionCode.substring(0, 5);
            }
            waybillReceiveAddress.setRegionCode(regionCode);
        }

        int addressId = waybillReceiveAddressService.insert(waybillReceiveAddress, driverLoginEcoUser);

        // 操作轨迹
        waybillOperateTrackService.insert(waybillReceiveAddress.getWaybillId(),
                OperateType.ADD_RECEIVE_ADDRESS, OperateApplication.DRIVER_SYS,
                null, driverLoginEcoUser);
        return waybillReceiveAddressService.getWaybillReceiveAddress(addressId);
    }

    /**
     * 司机修改目的地
     */
    @RequestMapping(value = "update/receive/address", method = RequestMethod.POST)
    @ResponseBody
    public void updateReceiveAddress(@RequestBody WaybillReceiveAddress waybillReceiveAddress,
            LoginEcoUser driverLoginEcoUser) {
        if (null == waybillReceiveAddress.getAddressId()) {
            throw new BusinessException("addressIdRequire", "waybillAddress.error.addressIdRequire");
        }
        if (null == waybillReceiveAddress.getWaybillId()) {
            throw new BusinessException("waybillIdRequire", "waybillAddress.error.waybillIdRequire");
        }

        if (StringUtils.isBlank(waybillReceiveAddress.getAddressName())) {
            throw new BusinessException("addressNameRequire", "waybillAddress.error.addressNameRequire");
        }

        if (StringUtils.isBlank(waybillReceiveAddress.getCoordinates())) {
            throw new BusinessException("coordinatesRequire", "waybillAddress.error.coordinatesRequire");
        }

        if (StringUtils.isBlank(waybillReceiveAddress.getAddressDetail())) {
            throw new BusinessException("addressDetailRequire", "waybillAddress.error.addressDetailRequire");
        }

        if (StringUtils.isBlank(waybillReceiveAddress.getRegionCode())) {
            String regionCode = regionTgmService.findRegionCodeByCoordinate(waybillReceiveAddress.getCoordinates());
            if (regionCode.length() > 5) {
                regionCode = regionCode.substring(0, 5);
            }
            waybillReceiveAddress.setRegionCode(regionCode);
        }

        waybillReceiveAddressService.update(waybillReceiveAddress, driverLoginEcoUser);

        // 操作轨迹
        waybillOperateTrackService.insert(waybillReceiveAddress.getWaybillId(),
                OperateType.UPDATE_RECEIVE_ADDRESS,
                OperateApplication.DRIVER_SYS, null, driverLoginEcoUser);
    }
    
    
    /**
     * 司机批量更新地址
     */
    @RequestMapping(value = "{waybillId}/receive/updateOrder", method = RequestMethod.POST)
    @ResponseBody
    public void batchUpdate(@PathVariable Integer waybillId,@RequestBody List<WaybillReceiveAddress> rows, DriverLoginUser driverLoginUser) {
        for(WaybillReceiveAddress waybillReceiveAddress : rows) {
            if (null == waybillReceiveAddress.getAddressId()) {
                throw new BusinessException("addressIdRequire", "waybillAddress.error.addressIdRequire");
            }
            if (null == waybillReceiveAddress.getSequence()) {
                throw new BusinessException("sequenceRequire", "序列不能为空");
            }
            waybillReceiveAddress.setWaybillId(waybillId);
        }
        waybillReceiveAddressService.updateBatchByPrimaryKeySelective(rows);
    }

    /**
     * 司机删除目的地
     */
    @RequestMapping(value = "del/{addressId}/receive/address", method = RequestMethod.GET)
    @ResponseBody
    public void delReceiveAddress(@PathVariable Integer addressId, LoginEcoUser driverLoginEcoUser) {
        WaybillReceiveAddress waybillReceiveAddress = waybillReceiveAddressService.getWaybillReceiveAddress(addressId);
        waybillReceiveAddressService.delete(addressId);

        // 操作轨迹
        waybillOperateTrackService.insert(waybillReceiveAddress.getWaybillId(),
                OperateType.DEL_RECEIVE_ADDRESS, OperateApplication.DRIVER_SYS,
                null, driverLoginEcoUser);
    }
    

    /**
     * 司机修改取货地
     */
    @RequestMapping(value = "update/delivery/address", method = RequestMethod.POST)
    @ResponseBody
    public void updateDeliveryAddress(@RequestBody WaybillDeliveryAddress waybillDeliveryAddress,
            LoginEcoUser driverLoginEcoUser) {
        waybillDeliveryAddressService.updateDeliveryAddress(waybillDeliveryAddress, driverLoginEcoUser);

        // 报备
        try {
            ReportInfo reportInfo = new ReportInfo();
            reportInfo.setReportInfoType(Constants.UPDATE_DELIVERY_ADDRESS_REPORT_TYPE);
            reportInfo.setWaybillId(waybillDeliveryAddress.getWaybillId());
            reportInfo.setFirstReportTime(new Date());

            // 司机信息
            Driver driver = vmsCommonService.loadDriverByUserId(driverLoginEcoUser.getUserId());
            if (null != driver) {
                DriverTenant driverTenant = vmsCommonService.loadDriverTenantByDriverId(driver.getDriverId(), driverLoginEcoUser);
                if (null != driverTenant) {
                    // 业务区域信息
                    reportInfo.setAreaCode(driverTenant.getAreaCode());
                    reportInfo.setTenantCode(driverTenant.getTenantCode());
                }
            }

            reportInfoService.insert(reportInfo, driverLoginEcoUser);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // 操作轨迹
        waybillOperateTrackService.insert(waybillDeliveryAddress.getWaybillId(),
                OperateType.UPDATE_DELIVERY_ADDRESS,
                OperateApplication.DRIVER_SYS, null, driverLoginEcoUser);
    }

    /**
     * 自动派单 预约单
     */
    @RequestMapping(value = "plan")
    @ResponseBody
    public ToAutoMatchWaybill plan() {
        List<String> rows = new ArrayList<String>();
        rows.add("000400");
        return waybillAutoMatchService.findToAutoMatchWaybillPlan(rows, Constants.SYS_LOGIN_USER);
    }

    /**
     * 客户经理-修改配送地
     *
     * @param addressList
     * @param id
     * @param loginEmployee
     */
    @RequestMapping(value = "{waybillId}/updateDeliveryPoint", method = RequestMethod.POST)
    @ResponseBody
    public void updateDeliveryPoint(@RequestBody List<WaybillReceiveAddress> addressList,
            @PathVariable("waybillId") Integer id, LoginEmployee loginEmployee) {
        // 派车中和待配送的运单才可以修改
        Waybill waybill = waybillService.getWaybill(id);
        if (waybill == null) {
            throw new BusinessException("waybillNull", "errors.notFound");
        }

        if (NumberUtils.compare(waybill.getStatusView(), Waybill.StatusView.WATING_RECEIVE.getCode()) != 0
                && NumberUtils.compare(waybill.getStatusView(), Waybill.StatusView.WATING_DELIVERY.getCode()) != 0) {
            throw new BusinessException("statusError", "errors.deliveryPointModify.statusError", "派车中和待配送");
        }

        if (com.giants.common.collections.CollectionUtils.isEmpty(addressList)) {
            return;
        }

        for (WaybillReceiveAddress addr : addressList) {
            addr.setWaybillId(id);
        }

        deliveryPointSupplementService.updateWaybillReceiveAddress(addressList, loginEmployee);

    }

    /**
     * 通过crmCustomerId获取tgm企业客户信息
     *
     * @param crmId
     * @return
     */
    private CustomerInfo crmCustomerToTgmCustomer(Integer crmId) {
        if (crmId == null) {
            return null;
        }
        CustomerInfo customerInfo = customerInfoService.findByCrmId(crmId);
        return customerInfo;
    }

    /**
     * 构造企业/用车人订单查询参数，
     *
     * @param condition
     */
    private void buildQueryParam(PageCondition condition) {
        Map<String, Object> param = condition.getFilters();
        if (MapUtils.isEmpty(param)) {
            return;
        }

        // 旧版本 queryDate 参数 适配
        if (param.get("startTime") == null && param.get("queryDate") != null) {
            // 支持搜索某一天的数据
            String queryDate = (String) param.get("queryDate");

            // 年月日
            waybillQueryUtil.parseYMD(param, queryDate);
            // 年月
            if (param.get("endTime") == null) {
                waybillQueryUtil.parseYM(param, queryDate);
            }
            // 年
            if (param.get("endTime") == null) {
                waybillQueryUtil.parseY(param, queryDate);
            }
        } else if (StringUtils.isNotBlank((String) param.get("startTime"))) {
            // 年月日
            String startTime = waybillQueryUtil.parseYMD((String) param.get("startTime"), true);
            // 默认为开始时间当天的结束
            // String endTime = waybillQueryUtil.parseYMD( (String) param.get("startTime") , false);
            String endTime = null;
            // 如果存在描述 则用描述的覆盖
            if (StringUtils.isNotBlank((String) param.get("endTime"))) {
                endTime = waybillQueryUtil.parseYMD((String) param.get("endTime"), false);
            }
            param.put("startTime", startTime);
            param.put("endTime", endTime);
        }

        // 用车要求
        waybillQueryUtil.buildAdditionalFunctionIds(param);

        // 以下条件必须同时满足
        param.put("_hasParams", false);
        String type = null;
        try {
            type = param.get("type").toString();
        } catch (Exception e) {
            return;
        } finally {
            param.remove("type");
        }
        // 判断参数是企业客户
        String custStr = null;
        try {
            custStr = param.get("customerId").toString();
        } catch (Exception e) {
            return;
        } finally {
            param.remove("customerId");
        }

        if (StringUtils.isNumeric(custStr) && StringUtils.equals("1", type)) {// 企业客户
            param.put("_hasParams", true);
            param.put("customerId", Integer.valueOf(custStr));
        } else if (StringUtils.isNumeric(custStr) && StringUtils.equals("2", type)) {// 用车人
            param.put("_hasParams", true);
            param.put("truckCustomerId", Integer.valueOf(custStr));
        } else if (StringUtils.isNumeric(custStr) && StringUtils.equals("3", type)) {// 项目
            param.put("_hasParams", true);
            param.put("projectId", Integer.valueOf(custStr));
        }

    }

    // 到达取货地或者目的地。更改其到达状态
    @ResponseBody
    @RequestMapping(value = "address/status/update", method = RequestMethod.POST)
    public void updateAddressStatus(@RequestBody AddressParamVo addressParamVo, LoginEcoUser driverLoginEcoUser) {
        if (addressParamVo.getAddressType() == null || addressParamVo.getAddressId() == null) {
            throw new BusinessException("addressIdAndAddressTypeRequire",
                    "waybillAddress.errors.addressIdAndAddressTypeRequire");
        }
        if (addressParamVo.getAddressType() == 1) {
            // 到达目的地
            WaybillReceiveAddress waybillReceiveAddress = waybillReceiveAddressService
                    .getWaybillReceiveAddress(addressParamVo.getAddressId());
            if (null != waybillReceiveAddress) {
                waybillReceiveAddress.setIsArrived(1);
                waybillReceiveAddressService.update(waybillReceiveAddress, driverLoginEcoUser);
            }
        } else if (addressParamVo.getAddressType() == 2) {
            // 到达取货点
            WaybillDeliveryAddress waybillDeliveryAddress = waybillDeliveryAddressService
                    .findByAddressId(addressParamVo.getAddressId());
            if (null != waybillDeliveryAddress) {
                waybillDeliveryAddress.setIsArrived(1);
                waybillDeliveryAddressService.update(waybillDeliveryAddress, driverLoginEcoUser);
            }
        }
    }

    /**
     * 完善信息接口
     */
    @RequestMapping(value = "{waybillId}/completeWaybillParam")
    @ResponseBody
    public void completeWaybillParam(@PathVariable Integer waybillId, @RequestBody WaybillParam waybillParam,
            DriverLoginUser driverLoginUser, LoginEcoUser driverLoginEcoUser) {
        waybillParamService.doCompleteWaybillParam(waybillId, waybillParam.getValuationConstJson(), driverLoginEcoUser);
    }

    /**
     * 经济人端 完善信息接口
     */
    @ResponseBody
    @RequestMapping(value = "{waybillId}/customer/completeWaybillParam", method = RequestMethod.POST)
    public void fillInWorkload(@PathVariable Integer waybillId, @RequestBody WaybillParam waybillParam,
            LoginEmployee loginEmployee) {
        waybillParamService.doCompleteWaybillParam(waybillId, waybillParam.getValuationConstJson(), loginEmployee);
    }

    /**
     * 用于判断运单状态
     *
     * @param waybillId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{waybillId}/base", method = RequestMethod.GET)
    public Waybill getWaybillBase(@PathVariable Integer waybillId) {
        Waybill waybill = waybillService.getWaybill(waybillId);
        if (waybill == null) return null;

        waybill.setEstimateDistance(null);
        waybill.setAreaName(null);
        waybill.setAssignCarFeedback(null);
        waybill.setAssignWaybillRemark(null);
        waybill.setUpdateFreightAuditStatus(null);
        waybill.setUpdateFreightRemark(null);
        waybill.setCreateTime(null);
        waybill.setLastUpdateTime(null);
        waybill.setFinishTime(null);
        waybill.setPlanDeliveryTime(null);
        waybill.setProjectName(null);
        return waybill;

    }

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {

    }

}
