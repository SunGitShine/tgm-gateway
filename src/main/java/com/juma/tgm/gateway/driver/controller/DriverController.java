/**
 *
 */
package com.juma.tgm.gateway.driver.controller;

import com.giants.cache.redis.RedisClient;
import com.giants.common.exception.BusinessException;
import com.juma.auth.user.domain.LoginEcoUser;
import com.juma.server.vm.service.vehicle.AmsServiceV2;
import com.juma.tgm.common.BaseUtil;
import com.juma.tgm.common.Constants;
import com.juma.tgm.driver.domain.Driver;
import com.juma.tgm.driver.domain.DriverLoginUser;
import com.juma.tgm.driver.service.DriverService;
import com.juma.tgm.gateway.common.AbstractController;
import com.juma.tgm.gateway.driver.controller.vo.DeliveryPointSupplementGroupByTime;
import com.juma.tgm.landing.waybill.service.DispatchingTruckService;
import com.juma.tgm.tools.service.VmsCommonService;
import com.juma.tgm.truck.domain.bo.DriverTruckInfoBo;
import com.juma.tgm.version.service.VersionService;
import com.juma.tgm.waybill.domain.DeliveryPointSupplement;
import com.juma.tgm.waybill.domain.WaybillNotice;
import com.juma.tgm.waybill.domain.WaybillParam;
import com.juma.tgm.waybill.domain.map.WaybillMapTracePoint;
import com.juma.tgm.waybill.domain.vo.DeliveryPointSupplementVo;
import com.juma.tgm.waybill.service.DeliveryPointSupplementService;
import com.juma.tgm.waybill.service.WaybillCommonService;
import com.juma.tgm.waybill.service.WaybillNoticeService;
import com.juma.tgm.waybill.service.WaybillParamService;
import com.juma.vms.driver.enumeration.RemindSwitchType;
import com.juma.vms.driver.enumeration.RemindSwitchValue;
import com.juma.vms.vendor.domain.Vendor;
import com.juma.vms.vendor.domain.VendorWhiteList;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author vencent.lu
 */
@Controller
@RequestMapping(value = "driver")
public class DriverController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(DriverController.class);
    @Resource
    private DriverService driverService;
    @Resource
    private WaybillParamService waybillParamService;
    @Resource
    private WaybillNoticeService waybillNoticeService;
    @Resource
    private RedisClient redisClient;
    @Resource
    private VersionService versionService;
    @Resource
    private DeliveryPointSupplementService deliveryPointSupplementService;
    @Resource
    private AmsServiceV2 amsServiceV2;
    @Resource
    private DispatchingTruckService dispatchingTruckService;
    @Resource
    private WaybillCommonService waybillCommonService;
    @Resource
    private VmsCommonService vmsCommonService;

    /**
     * 司机确认接单
     */
    @ResponseBody
    @RequestMapping(value = "{waybillId}/accept", method = RequestMethod.GET)
    public void doDriverAnswerWaybill(@PathVariable Integer waybillId, LoginEcoUser driverLoginEcoUser) {
        dispatchingTruckService.doDriverAnswerWaybill(waybillId, driverLoginEcoUser);
    }

    /**
     * 待配送的点
     */
    @ResponseBody
    @RequestMapping(value = "delivery/sequence", method = RequestMethod.GET)
    public WaybillMapTracePoint findWaybillMapPoint(@RequestParam(defaultValue = "1") Integer toDayOrTomorrow
            , DriverLoginUser driverLoginUser
            , LoginEcoUser driverLoginEcoUser) {
        return waybillCommonService.findWaybillMapPoint(driverLoginUser.getDriverId(), toDayOrTomorrow, driverLoginEcoUser);
    }

    @RequestMapping(value = "parkingAddress", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> parking(DriverLoginUser driverLoginUser, LoginEcoUser driverLoginEcoUser) {
        Map<String, String> result = new HashMap<String, String>();
        Driver driver = driverService.getDriver(driverLoginUser.getDriverId());
        com.juma.server.vm.domain.Driver amsDriver = amsServiceV2.getDriverById(driver.getAmsDriverId());
        if (amsDriver != null) {
            result.put("parkAddress", amsDriver.getParkAddress());
            if (amsDriver.getLongitude() != null && amsDriver.getLatitude() != null) {
                result.put("coordinates",
                        amsDriver.getLongitude().doubleValue() + "," + amsDriver.getLatitude().doubleValue());
            }
        }
        return result;
    }

    @RequestMapping(value = "getDriverLoginUser", method = RequestMethod.GET)
    @ResponseBody
    public DriverLoginUser getDriverLoginUser(DriverLoginUser driverLoginUser, LoginEcoUser driverLoginEcoUser) {
        Driver driver = driverService.findDriverByUserId(driverLoginEcoUser.getUserId());
        if (null != driver) {
            driverLoginUser.setIsAcceptAllocateOrders(driver.getWhetherAcceptAllocateOrder());
            Integer userId = driverLoginEcoUser.getUserId();
            driverLoginUser.setLoginEcoUser(driverLoginEcoUser);
            driverLoginUser.setWaybillIdNeedConfirmCeivedFreight(getWaybillId(
                    Constants.APP_USER_CONFIRM_RECEIVED_FREIGHT + userId + driverLoginEcoUser.getTenantId()));
            Integer waybillId = getWaybillId(
                    Constants.APP_USER_PRVEFIEX + Constants.STAR_DRIVER + userId + driverLoginEcoUser.getTenantId());
            driverLoginUser.setWaybillIdNeedToEvaluate(waybillId);
            driverLoginUser.setDriverStatus(driver.getStatus());
            driverLoginUser.setEmployeeClass(driver.getEmployeeClass());
            driverLoginUser.setHeadPortrait(driver.getHeadPortrait());
            driverLoginUser.setVersionCheck(versionService.checkVersion());
            driverLoginUser.setVersion(versionService.apkVersion());
        } else {
            LoginEcoUser loginEcoUser = new LoginEcoUser();
            loginEcoUser.setUserId(-1);
            loginEcoUser.setEcoUserId(-1);
            driverLoginUser.setLoginEcoUser(loginEcoUser);

            driverLoginUser.setDriverId(-1);
            driverLoginUser.setIsAcceptAllocateOrders(-1);
            driverLoginUser.setDriverStatus(-1);
            driverLoginUser.setEmployeeClass(-1);
            driverLoginUser.setVersionCheck(versionService.checkVersion());
            driverLoginUser.setVersion(versionService.apkVersion());
        }
        return driverLoginUser;
    }

    // 获取waybillId
    private Integer getWaybillId(String key) {
        Serializable serializable = redisClient.get(key);
        if (null != serializable) {
            String waybillIdStr = String.valueOf(serializable);
            return BaseUtil.getInt(waybillIdStr);
        }
        return 0;
    }

    /**
     * 司机接单开关
     */
    @ResponseBody
    @RequestMapping(value = "{accept}/accept/allocate/order", method = RequestMethod.POST)
    public void updateAcceptAllocateOrder(@PathVariable Integer accept, LoginEcoUser driverLoginEcoUser) {
        com.juma.vms.driver.domain.Driver driver = vmsCommonService.loadDriverByUserId(driverLoginEcoUser.getUserId());
        vmsCommonService.updateDriverIsAcceptAllocateOrder(driver.getDriverId(), (accept == 0 ? 0 : 1), driverLoginEcoUser);
    }

    /**
     * 根据运单ID查询司机货车信息
     *
     * @param waybillId
     * @return
     */
    @RequestMapping(value = "getDriverTruckInfoByWaybillId/{waybillId}", method = RequestMethod.GET)
    @ResponseBody
    public DriverTruckInfoBo getDriverTruckInfoByWaybillId(@PathVariable Integer waybillId) {
        return driverService.findDriverTruckInfoByWaybillId(waybillId);
    }

    /**
     * 司机状态认证 可接单检查
     */
    @RequestMapping(value = "checkStatus", method = RequestMethod.GET)
    @ResponseBody
    public void checkStatus(DriverLoginUser driverLoginUser, LoginEcoUser driverLoginEcoUser) {
        driverService.checkDriverAcceptAllocateOrder(driverLoginUser.getDriverId(), driverLoginEcoUser);
    }

    /**
     * 得到已通知司机的数量
     */
    @RequestMapping(value = "{waybillId}/getDriverNoticeNum", method = RequestMethod.POST)
    @ResponseBody
    public Integer getDriverNoticeNum(@PathVariable Integer waybillId) {
        WaybillNotice notice = waybillNoticeService.findBy(waybillId);
        if (notice == null)
            return 8;
        return notice.getNoticeDriverNum() * 3 + 5;
    }

    /**
     * 获取司机基础信息
     */
    @RequestMapping(value = "base/info", method = RequestMethod.GET)
    @ResponseBody
    public Driver driverBaseInfo(LoginEcoUser driverLoginEcoUser) {
        return driverService.findDriverByUserId(driverLoginEcoUser.getUserId());
    }

    /**
     * 添加线路修改信息
     *
     * @param point
     */
    @RequestMapping(value = "/{waybillId}/addSupplementPoint", method = RequestMethod.POST)
    @ResponseBody
    public List<Integer> addSupplementPoint(@RequestBody DeliveryPointSupplementVo point,
                                            @PathVariable(value = "waybillId") Integer id, LoginEcoUser driverLoginEcoUser) {
        if (point == null) {
            throw new BusinessException("DeliveryPointSupplementNull", "errors.paramCanNotNull");
        }

        if (CollectionUtils.isEmpty(point.getDeliveryPointSupplements())) {
            throw new BusinessException("DeliveryPointSupplementNull", "errors.paramCanNotNullWithName", "配送点");
        }

        if (point.getReportInfoParam() == null) {
            throw new BusinessException("ReportInfoNull", "errors.paramCanNotNullWithName", "报备信息");
        }

        for (DeliveryPointSupplement dps : point.getDeliveryPointSupplements()) {
            dps.setWaybillId(id);
        }
        // 线路修改信息

        List<Integer> ids = deliveryPointSupplementService.addBatch(point.getDeliveryPointSupplements(),
                driverLoginEcoUser);

        // 添加上传时间
        WaybillParam param = waybillParamService.findByWaybillId(id);
        if (null == param) {
            param = new WaybillParam();
            param.setWaybillId(id);
            param.setUploadDeliveryPointSupplementTime(new Date());
            waybillParamService.insert(param, driverLoginEcoUser);
        } else {
            param.setUploadDeliveryPointSupplementTime(new Date());
            waybillParamService.update(param, driverLoginEcoUser);
        }

        return ids;
    }

    /**
     * 获取已经上传的线路修改信息数量
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{waybillId}/countSupplementPoint", method = RequestMethod.GET)
    @ResponseBody
    public int countSupplementPoint(@PathVariable(value = "waybillId") Integer id) {
        return deliveryPointSupplementService.countDeliveryPointSupplement(id);
    }

    /**
     * @Title: supplementPoint @Description: 配送单 @return: List
     *         <DeliveryPointSupplement> @throws
     */
    @RequestMapping(value = "/{waybillId}/supplementPoints", method = RequestMethod.GET)
    @ResponseBody
    public Object[] supplementPoint(@PathVariable(value = "waybillId") Integer id) {

        Map<Date, List<String>> map = new TreeMap<Date, List<String>>();

        List<DeliveryPointSupplement> rows = deliveryPointSupplementService.getByWayBill(id);
        for (DeliveryPointSupplement row : rows) {
            Date key = row.getCreateTime();
            if (map.containsKey(key)) {
                map.get(key).add(row.getImg());
            } else {
                List<String> _rows = new ArrayList<String>();
                if (StringUtils.isNotBlank(row.getImg())) {
                    _rows.add(row.getImg());
                    map.put(key, _rows);
                }
            }
        }
        List<DeliveryPointSupplementGroupByTime> group = new ArrayList<DeliveryPointSupplementGroupByTime>();
        Iterator<Entry<Date, List<String>>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Date, List<String>> entry = it.next();
            DeliveryPointSupplementGroupByTime groupby = new DeliveryPointSupplementGroupByTime();
            groupby.setUploadTime(entry.getKey());
            groupby.setImgList(entry.getValue());
            group.add(groupby);
        }
        Object[] arrays = group.toArray();
        CollectionUtils.reverseArray(arrays);
        return arrays;

    }

    @ResponseBody
    @RequestMapping(value = "{remindSwitchType}/{remindSwitchValue}/remind/switch", method = RequestMethod.GET)
    public void remindSwitch(@PathVariable String remindSwitchType, @PathVariable Integer remindSwitchValue,
                             LoginEcoUser driverLoginEcoUser) {
        if (StringUtils.isBlank(remindSwitchType) || null == remindSwitchValue) {
            throw new BusinessException("errors.validation.failure", "errors.validation.failure");
        }

        RemindSwitchType switchType = RemindSwitchType.valueOf(remindSwitchType.toUpperCase());
        if (null == switchType) {
            throw new BusinessException("errors.validation.failure", "errors.validation.failure");
        }

        Driver driver = driverService.findDriverByUserId(driverLoginEcoUser.getUserId());
        if (null == driver) {
            throw new BusinessException("driverNotfound", "driver.error.not.found");
        }

        for (RemindSwitchValue r : RemindSwitchValue.values()) {
            if (remindSwitchValue != r.getCode()) {
                continue;
            }
            vmsCommonService.updateRemindSwitch(driver.getDriverId(), r, switchType, driverLoginEcoUser);
            return;
        }
    }

    /**
     * 根据手机号判断是不是司机
     */
    @ResponseBody
    @RequestMapping(value = "{phoneNumber}/checkByPhoneIsDriver", method = RequestMethod.GET)
    public Boolean checkByPhoneIsDriver(@PathVariable String phoneNumber) {
        if (StringUtils.isBlank(phoneNumber)) {
            throw new BusinessException("errors.required", "errors.required", "手机号");
        }

        com.juma.vms.driver.domain.Driver driver = vmsCommonService.loadDriverByPhone(phoneNumber);
        Vendor vendor = vmsCommonService.loadVendorByPhone(phoneNumber);
        return driver != null || vendor != null;
    }

    /**
     * 根据承运商ID判断承运商是不是在白名单中
     */
    @ResponseBody
    @RequestMapping(value = "{vendorId}/checkVendorInWhiteList", method = RequestMethod.GET)
    public Boolean checkByPhoneIsDriver(@PathVariable Integer vendorId) {
        if (null == vendorId) {
            throw new BusinessException("vendorIdMust", "errors.common.prompt", "参数承运商ID不能为空");
        }

        return vmsCommonService.loadVendorWhiteListByVendorId(vendorId) == null ? false : true;
    }
}
