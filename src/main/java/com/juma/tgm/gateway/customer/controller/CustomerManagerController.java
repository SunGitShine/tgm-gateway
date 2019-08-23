/**
 *
 */
package com.juma.tgm.gateway.customer.controller;

import com.giants.cache.redis.RedisClient;
import com.giants.common.exception.BusinessException;
import com.giants.common.lang.exception.CategoryCodeFormatException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.giants.common.tools.PageQueryCondition;
import com.juma.auth.authority.service.AuthorityService;
import com.juma.auth.conf.domain.BusinessAreaNode;
import com.juma.auth.conf.domain.Permission;
import com.juma.auth.conf.service.PermissionService;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.employee.service.EmployeeService;
import com.juma.auth.user.domain.User;
import com.juma.auth.user.service.UserService;
import com.juma.cms.wx.domain.Chanel;
import com.juma.cms.wx.service.ChanelService;
import com.juma.conf.domain.ConfParamOption;
import com.juma.conf.domain.Region;
import com.juma.conf.service.ConfParamService;
import com.juma.conf.service.RegionService;
import com.juma.crm.customer.domain.ConsignorBaseInfoVo;
import com.juma.crm.customer.domain.ConsignorContactsInfo;
import com.juma.crm.customer.domain.ConsignorCustomerElimination;
import com.juma.crm.customer.domain.ConsignorCustomerInfo;
import com.juma.crm.customer.domain.ConsignorCustomerWholeInfoVo;
import com.juma.crm.customer.domain.ConsignorVisitRecord;
import com.juma.crm.customer.domain.ConsignorVisitRecordVo;
import com.juma.crm.customer.domain.CustomerStatusCount;
import com.juma.crm.customer.domain.ProductsLableInfo;
import com.juma.crm.support.service.Crm4TmsService;
import com.juma.tgm.authority.service.TgmUserCenterService;
import com.juma.tgm.cityManage.domain.CityManage;
import com.juma.tgm.cityManage.domain.CityManageInfo;
import com.juma.tgm.cityManage.service.CityManageService;
import com.juma.tgm.common.BaseUtil;
import com.juma.tgm.common.Constants;
import com.juma.tgm.crm.domain.ConsignorCustomerInfoVo;
import com.juma.tgm.crm.domain.ConsignorCustomerWholeInfoWithTgmInfo;
import com.juma.tgm.crm.domain.ConsignorVisitRecordTgmVo;
import com.juma.tgm.crm.domain.CustomerInfo;
import com.juma.tgm.crm.domain.CustomerInfoBo;
import com.juma.tgm.crm.service.CustomerInfoService;
import com.juma.tgm.customer.domain.CustomerLoginUser;
import com.juma.tgm.customer.domain.TruckCustomer;
import com.juma.tgm.customer.domain.TruckCustomerBo;
import com.juma.tgm.customer.domain.vo.CargoOwnerCustomerVo;
import com.juma.tgm.customer.domain.vo.CargoOwnerVo;
import com.juma.tgm.customer.domain.vo.ScatteredCustomerVo;
import com.juma.tgm.customer.domain.vo.SearchEnterpriseUserAndCargoOwner;
import com.juma.tgm.customer.service.CustomerManagerService;
import com.juma.tgm.flight.domain.bo.TransportCapacityBo;
import com.juma.tgm.gateway.cargoOwner.vo.ScatteredWaybillCargoOwnerDetailVo;
import com.juma.tgm.gateway.common.AbstractController;
import com.juma.tgm.gateway.customer.CustomerManagerBusinessModule.WaybillTrackDetailBuilder;
import com.juma.tgm.gateway.customer.bo.WaybillTrackDetailBo;
import com.juma.tgm.gateway.customer.bo.WaybillTrackOverviewBo;
import com.juma.tgm.gateway.customer.vo.RegionVo;
import com.juma.tgm.gateway.waybill.controller.vo.CityManageInfoVo;
import com.juma.tgm.project.domain.Project;
import com.juma.tgm.scatteredWaybill.service.ScatteredWaybillService;
import com.juma.tgm.truck.domain.TruckFleet;
import com.juma.tgm.truck.domain.bo.DriverTruckInfoBo;
import com.juma.tgm.truck.domain.bo.LogisticsProductBo;
import com.juma.tgm.truck.domain.vo.TruckFleetQueryVo;
import com.juma.tgm.truck.service.TruckFleetService;
import com.juma.tgm.truck.service.TruckFleetTruckService;
import com.juma.tgm.truck.vo.TruckFleetTruckVo;
import com.juma.tgm.user.domain.CurrentUser;
import com.juma.tgm.vendor.domain.VendorMapping;
import com.juma.tgm.vendor.service.VendorMappingService;
import com.juma.tgm.version.service.VersionService;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillDetailInfo;
import com.juma.tgm.waybill.domain.WaybillOperateTrack;
import com.juma.tgm.waybill.domain.vo.CustomerManagerDebtDetailVo;
import com.juma.tgm.waybill.domain.vo.CustomerManagerDebtOverviewVo;
import com.juma.tgm.waybill.domain.vo.CustomerManagerDebtVo;
import com.juma.tgm.waybill.domain.vo.CustomerPerformanceVo;
import com.juma.tgm.waybill.domain.vo.ScatteredWaybillViewVo;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateType;
import com.juma.tgm.waybill.service.WaybillOperateTrackService;
import com.juma.tgm.waybill.service.WaybillService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author vencent.lu
 */
@Controller
@RequestMapping(value = "truckCustomer")
// @RequestMapping(value = "customer/manager")
public class CustomerManagerController extends AbstractController {

    /**
     * crm客户类型key
     */
    private static final String CRM_CONSIGNOR_TYPE = "CRM_CONSIGNOR_TYPE";

    /**
     * crm客户性质key
     */
    private static final String CRM_ENTERPRISE_NATURE = "CRM_ENTERPRISE_NATURE";

    /**
     * crm行业key
     */
    private static final String CRM_OWNED_INDUSTRY = "CRM_OWNED_INDUSTRY";
    /**
     * 经纪人渠道
     */
    private final static String SOURCE_CHANNEL_CODE = "CUSTOMER_MANAGER";
    /**
     * 评估结果
     */
    private final static String CRM_VISIT_WAY = "CRM_VISIT_WAY";

    private String[] regionCode = new String[]{"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
        "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
        "30", "31", "32", "33"};

    @Resource
    private TruckFleetService truckFleetService;
    @Resource
    private TruckFleetTruckService truckFleetTruckService;
    @Resource
    private CustomerInfoService customerInfoService;
    @Resource
    private VersionService versionService;
    @Resource
    private RedisClient redisClient;
    @Resource
    private RegionService regionService;
    @Resource
    private ChanelService chanelService;
    @Resource
    private TgmUserCenterService tgmUserCenterService;
    @Resource
    private CustomerManagerService customerManagerService;
    @Resource
    private AuthorityService authorityService;
    @Resource
    private UserService userService;
    @Resource
    private WaybillService waybillService;
    @Resource
    private ConfParamService confParamService;
    @Resource
    private WaybillOperateTrackService waybillOperateTrackService;
    @Resource
    private WaybillTrackDetailBuilder waybillTrackDetailBuilder;
    @Resource
    private CityManageService cityManageService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private EmployeeService employeeService;

    @Resource
    private ScatteredWaybillService scatteredWaybillService;

    @Resource
    private VendorMappingService vendorMappingService;

    @Resource
    private Crm4TmsService crm4TmsService;

    /**
     * 车队搜索参数对象
     */
    public static class TruckFleetPageQueryCondition extends PageQueryCondition<TruckFleetQueryVo> {
        public TruckFleetPageQueryCondition() {
            super();
        }
    }

    @RequestMapping(value = "customerManager/profile/update", method = RequestMethod.POST)
    @ResponseBody
    public void updateProfile(@RequestBody User newUser, LoginEmployee loginEmployee) {
        User user = userService.loadUser(loginEmployee.getUserId());
        user.setPassword(null);
        user.setName(newUser.getName());
        user.setIcon(newUser.getIcon());
        userService.updateUser(user, loginEmployee);
    }

    /**
     * use for customer manager client
     * <p/>
     * 客户经理用户信息
     */
    @RequestMapping(value = "manager/loginUser/info", method = RequestMethod.GET)
    @ResponseBody
    public CustomerLoginUser customerManagerLoginUser(CustomerLoginUser customerLoginUser,
                                                      LoginEmployee loginEmployee) {
        // 获取待评价运单ID
        Integer userId = loginEmployee.getUserId();
        Integer waybillId = getWaybillId(
            Constants.APP_USER_PRVEFIEX + Constants.STAR_CUSTOMER + userId + loginEmployee.getTenantId());
        customerLoginUser.setWaybillIdNeedToEvaluate(waybillId);

        // 版本号
        customerLoginUser.setVersionCheck(versionService.checkVersion());
        //是否展示物流标签
        customerLoginUser.setShowLogisticsLabel(crm4TmsService.isShowConfigure(ProductsLableInfo.configureEnum.LOGISTICS_PRODUCTS.getCode(), loginEmployee));

        try {
            List<Permission> permissions = permissionService.findPermissions(Constants.AUTH_KEY_TGM_MANAGE);
            for (Permission permission : permissions) {
                if (employeeService.isPermission(Constants.AUTH_KEY_TGM_MANAGE, permission.getPermissionKey(),
                    loginEmployee)) {
                    customerLoginUser.getPermissionKeyList().add(permission.getPermissionKey());
                }
            }
        } catch (Exception e) {
        }

        // 有无车队
        List<TruckFleet> list = truckFleetService.listTruckFleetByUserId(loginEmployee);
        if (list.isEmpty()) {
            customerLoginUser.setHasTruckFleet(false);
        }

        for (TruckFleet truckFleet : list) {
            List<TruckFleetTruckVo> truckFleetTruckList = truckFleetTruckService
                .listByTruckFleetId(truckFleet.getTruckFleetId());
            if (!truckFleetTruckList.isEmpty()) {
                customerLoginUser.setHasTruckFleet(true);
                break;
            }
        }
        return customerLoginUser;
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
     * 获得用车人的车队信息
     */
    @RequestMapping(value = "getTruckFleetByCustomer", method = RequestMethod.GET)
    @ResponseBody
    public Page<DriverTruckInfoBo> getTruckFleet(Integer waybillId, Integer pageNo, Integer pageSize,
                                                 LoginEmployee loginEmployee) {
        return customerManagerService.findTruckFleetBy(waybillId, pageNo, pageSize, loginEmployee);
    }

//    /**
//     * 大客户列表
//     */
//    @RequestMapping(value = "customerInfoList", method = RequestMethod.POST)
//    @ResponseBody
//    public Page<ConsignorCustomerInfoVo> customerInfoList(@RequestBody PageCondition pageCondition,
//            LoginEmployee loginEmployee) {
//        // 只展示当前登录人的客户列表
//        pageCondition.put("userId", loginEmployee.getUserId());
//        this.buildCrmStatusCode(pageCondition);
//
//        return buildLocalCustomerInfo(
//                customerInfoService.searchCrmCustomerWithMultipleStatus(pageCondition, loginEmployee));
//    }

    /**
     * 下单时使用的企业客户列表
     *
     * @param pageCondition
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "customerInfoForBill", method = RequestMethod.POST)
    @ResponseBody
    public Page<com.juma.tgm.crm.domain.CustomerInfo> customerInfoForBill(@RequestBody PageCondition pageCondition,
                                                                          LoginEmployee loginEmployee) {
        // 下单时可见的企业客户状态由客户端决定
        this.buildCrmStatusCode(pageCondition);
        pageCondition.getFilters().put("customerManagerUserId", loginEmployee.getEmployeeId());
        return customerInfoService.findCustomerFromTgm(pageCondition, loginEmployee);

    }

    private void buildCrmStatusCode(@RequestBody PageCondition pageCondition) {
        Map<String, Object> params = pageCondition.getFilters();

        if (MapUtils.isEmpty(params)) {
            throw new BusinessException("statusNull", "errors.paramError");
        }

        List<String> statusStr = (List<String>) params.get("statusList");
        List<Byte> codes = null;
        try {
            codes = this.crmCustomerStatusStr2Code(statusStr);
        } catch (Exception e) {
            logger.error("客户状态转换错误:", e);
            throw new BusinessException("statusError", "errors.paramError");
        }
        if (CollectionUtils.isEmpty(codes)) {
            throw new BusinessException("statusNull", "errors.paramError");
        }
        pageCondition.getFilters().put("statusList", codes);

        String nameKey = null;
        try {
            nameKey = (String) params.get("name");
        } catch (Exception e) {
            // 参数为空忽略
        }

        if (StringUtils.isBlank(nameKey)) {
            return;
        }
        pageCondition.getFilters().put("customerName", nameKey);
        pageCondition.getFilters().put("contactsName", nameKey);

    }

    /**
     * 通过用车人名称和企业名称获取列表
     *
     * @param pageCondition
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "searchEnterpriseAndContact", method = RequestMethod.POST)
    @ResponseBody
    public Page<SearchEnterpriseUserAndCargoOwner> searchEnterpriseAndContact(@RequestBody PageCondition pageCondition,
                                                                              LoginEmployee loginEmployee) {
        this.buildCrmStatusCode(pageCondition);
        Map<String, Object> params = pageCondition.getFilters();
        if (params.get("name") == null) {
            return null;
        }

        Page<SearchEnterpriseUserAndCargoOwner> pageData = customerInfoService
            .searchEnterpriseUserAndCargoOwner(pageCondition, loginEmployee);
        if (CollectionUtils.isNotEmpty(pageData.getResults())) {
            this.buildSourceCode(pageData.getResults());
        }

        return pageData;
    }

    private void buildSourceCode(Collection<SearchEnterpriseUserAndCargoOwner> datas) {
        if (CollectionUtils.isEmpty(datas)) {
            return;
        }

        for (SearchEnterpriseUserAndCargoOwner ec : datas) {
            // 企业客户展示来源
            if (NumberUtils.compare(SearchEnterpriseUserAndCargoOwner.INFO_TYPE_ENTERPRISE, ec.getType()) == 0) {
                Chanel chanel = chanelService.findByCode(ec.getSourceCode());
                if (chanel == null) {
                    continue;
                }
                ec.setCustomerName(ec.getCustomerName() + "(" + chanel.getName() + ")");
            }

        }
    }

    /**
     * 设置tgm系统的数据
     *
     * @param customerInfoPage
     * @return
     */
    private Page<ConsignorCustomerInfoVo> buildLocalCustomerInfo(Page<ConsignorCustomerInfo> customerInfoPage) {
        Collection<ConsignorCustomerInfo> originData = customerInfoPage.getResults();
        Page<ConsignorCustomerInfoVo> newPage = new Page<>();
        newPage.setTotal(customerInfoPage.getTotal());
        newPage.setPageSize(customerInfoPage.getPageSize());
        newPage.setPageNumCount(customerInfoPage.getPageNumCount());
        newPage.setPageNo(customerInfoPage.getPageNo());
        Map<String, String> mapChanel = mapBuildChanel();
        if (CollectionUtils.isEmpty(originData)) {
            return newPage;
        }
        Collection<ConsignorCustomerInfoVo> newData = new ArrayList<>();
        ConsignorCustomerInfoVo tgmInfo = null;
        for (ConsignorCustomerInfo cci : originData) {
            tgmInfo = new ConsignorCustomerInfoVo();
            // 复制原有属性
            BeanUtils.copyProperties(cci, tgmInfo);

            // 通过crm CustomerId设置tgm信息
            CustomerInfo localInfo = customerInfoService.findByCrmId(cci.getCustomerId());
            if (localInfo != null) {
                tgmInfo.setTgmCustomerId(localInfo.getCustomerId());
                tgmInfo.setProjectCheckOut(localInfo.getIsProjectCheckOut());
            }
            // 客户渠道
            if (StringUtils.isNotBlank(cci.getSourceChannelCode())) {
                tgmInfo.setSourceChannelName(mapChanel.get(cci.getSourceChannelCode()));
            }
            newData.add(tgmInfo);
        }
        newPage.setResults(newData);
        return newPage;
    }

    // 获取客户经理渠道下的所有渠道
    private Map<String, String> mapBuildChanel() {
        Map<String, String> mapChanel = new HashMap<String, String>();
        List<Chanel> listChanel = chanelService.findChildrenByCode(SOURCE_CHANNEL_CODE);
        if (CollectionUtils.isNotEmpty(listChanel)) {
            for (Chanel chanel : listChanel) {
                mapChanel.put(chanel.getCode(), chanel.getName());
            }
        }
        return mapChanel;
    }

    /**
     * 缓存值
     */
    @ResponseBody
    @RequestMapping(value = "getRedis", method = RequestMethod.GET)
    public Object checkNotice(@RequestParam(value = "key") String key,
                              @RequestParam(value = "type", required = false) String type) {
        if (StringUtils.equals("hash", type)) {
            return redisClient.hgetall(key);
        } else {
            return redisClient.get(key);
        }
    }

    /**
     * 获得用车人的个人信息
     */
    @RequestMapping(value = "getTruckCustomerInfo", method = RequestMethod.GET)
    @ResponseBody
    public TruckCustomerBo getManagerInfo(LoginEmployee loginEmployee) {
        return customerManagerService.getCustomerManagerForClientUserCenter(loginEmployee);
    }

    /**
     * 按状态获取crm客户列表
     *
     * @param pageCondition
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "getCrmCustomers", method = RequestMethod.POST)
    @ResponseBody
    public Page<ConsignorCustomerWholeInfoVo> getCrmCustomers(@RequestBody PageCondition pageCondition,
                                                              LoginEmployee loginEmployee) {

        // 只展示当前登录人的客户列表
        pageCondition.getFilters().put("userId", loginEmployee.getEmployeeId());
        Page<ConsignorCustomerWholeInfoVo> pageData = customerInfoService.searchCrmCustomer(pageCondition,
            loginEmployee);
        Collection<ConsignorCustomerWholeInfoVo> rstData = pageData.getResults();
        if (CollectionUtils.isEmpty(rstData)) {
            return pageData;
        }

        for (ConsignorCustomerWholeInfoVo wholeInfoVo : rstData) {
            ConsignorCustomerInfoVo crmCustomerInfoVo = this.buildBasicInfoVo(wholeInfoVo.getConsignorCustomerInfo(),
                loginEmployee);
            wholeInfoVo.setConsignorCustomerInfo(crmCustomerInfoVo);
            this.buildListViewData(crmCustomerInfoVo);
        }

        return pageData;
    }

    /**
     * 组装展示信息
     *
     * @param customerVo
     */
    private void buildListViewData(ConsignorCustomerInfoVo customerVo) {
        if (NumberUtils.compare(customerVo.getStatus(),
            com.juma.crm.customer.domain.CustomerInfo.CustomerStatus.SIGN_SOON.getValue()) == 0) {
            // 已成交客户，获取成单数量
            CustomerInfo tgmCust = customerInfoService.findByCrmId(customerVo.getCustomerId());
            if (tgmCust != null) {
                customerVo.setWayBillCount(
                    tgmCust.getWaybillCount() == null ? "0" : tgmCust.getWaybillCount().toString());
            }
        } else if (NumberUtils.compare(customerVo.getStatus(),
            com.juma.crm.customer.domain.CustomerInfo.CustomerStatus.ELIMINATED.getValue()) == 0) {
            // 已淘汰客户，获取淘汰原因
            this.buildEliminationReason(customerVo);
        }

    }

    /**
     * 组装淘汰信息
     *
     * @param customerVo
     */
    private void buildEliminationReason(ConsignorCustomerInfoVo customerVo) {
        if (customerVo == null) {
            return;
        }

        ConsignorCustomerElimination record = customerInfoService
            .findLastEliminationByCustomerId(customerVo.getCustomerId());
        if (record != null) {
            customerVo.setEliminationReason(record.getEliminationReason());
        }
    }

    /**
     * 按条件获取各个状态下客户数量
     *
     * @param pageCondition
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "statusCounts", method = RequestMethod.POST)
    @ResponseBody
    public List<CustomerStatusCount> statusCounts(@RequestBody PageCondition pageCondition,
                                                  LoginEmployee loginEmployee) {
        pageCondition.getFilters().put("userId", loginEmployee.getEmployeeId());
        return customerInfoService.countCustomer(pageCondition, loginEmployee);
    }

    /**
     * 获取企业客户详情
     *
     * @param crmId
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "{crmId}/detail", method = RequestMethod.POST)
    @ResponseBody
    public ConsignorCustomerWholeInfoWithTgmInfo getConsignorCustomerWholeForView(
        @PathVariable(value = "crmId") Integer crmId, LoginEmployee loginEmployee) {
        ConsignorCustomerWholeInfoWithTgmInfo consignorCustomerWholeInfoWithTgmInfo = customerInfoService
            .getConsignorCustomerWholeInfoWithTgmInfo(crmId, loginEmployee);
        ConsignorCustomerInfo consignorCustomerInfo = this
            .buildBasicInfoVo(consignorCustomerWholeInfoWithTgmInfo.getConsignorCustomerInfo(), loginEmployee);
        consignorCustomerWholeInfoWithTgmInfo.setConsignorCustomerInfo(consignorCustomerInfo);
        consignorCustomerWholeInfoWithTgmInfo.setConsignorVisitRecordTgmVos(
            this.buildVisitRecodeVo(consignorCustomerWholeInfoWithTgmInfo.getConsignorVisitRecords()));
        consignorCustomerWholeInfoWithTgmInfo.setConsignorVisitRecords(null);
        return consignorCustomerWholeInfoWithTgmInfo;
    }

    /**
     * 新增企业客户信息
     *
     * @param consignorBaseInfoVo
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Integer addCustomerInfo(@RequestBody ConsignorBaseInfoVo consignorBaseInfoVo, LoginEmployee loginEmployee) {
        CustomerInfoBo bo = new CustomerInfoBo();
        bo.setConsignorBaseInfoVo(consignorBaseInfoVo);
        CustomerInfo info = customerInfoService.addCustomerInfo(bo, loginEmployee);

        return info.getCrmCustomerId();
    }

    /**
     * 修改联系人
     *
     * @param consignorContactsInfo
     * @param loginEmployee
     */
    @RequestMapping(value = "updateContact", method = RequestMethod.POST)
    @ResponseBody
    public void updateCrmContact(@RequestBody ConsignorContactsInfo consignorContactsInfo,
                                 LoginEmployee loginEmployee) {
        customerInfoService.updateContact(consignorContactsInfo, loginEmployee);
    }

    /**
     * 新增联系人
     *
     * @param consignorContactsInfo
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "addCrmContact", method = RequestMethod.POST)
    @ResponseBody
    public ConsignorContactsInfo addCrmContact(@RequestBody ConsignorContactsInfo consignorContactsInfo,
                                               LoginEmployee loginEmployee) {
        return customerInfoService.addContactsInfo(consignorContactsInfo, loginEmployee);
    }

    /**
     * 删除联系人
     *
     * @param consignorContactsInfo
     * @param loginEmployee
     */
    @RequestMapping(value = "deleteCrmContact", method = RequestMethod.POST)
    @ResponseBody
    public void deleteCrmContact(@RequestBody ConsignorContactsInfo consignorContactsInfo,
                                 LoginEmployee loginEmployee) {
        customerInfoService.deleteContactsInfo(consignorContactsInfo, loginEmployee);
    }

    /**
     * 新增拜访记录
     *
     * @param consignorVisitRecordVo
     * @param loginEmployee
     */
    @RequestMapping(value = "addVisitationRecord", method = RequestMethod.POST)
    @ResponseBody
    public void addVisitationRecord(@RequestBody ConsignorVisitRecordVo consignorVisitRecordVo,
                                    LoginEmployee loginEmployee) {
        customerInfoService.saveVisitationRecord(consignorVisitRecordVo, loginEmployee);
    }

    /**
     * 更新货主基础信息
     *
     * @param consignorBaseInfoVo
     * @param loginEmployee
     */
    @RequestMapping(value = "updateBaseInfo", method = RequestMethod.POST)
    @ResponseBody
    public void updateBaseInfo(@RequestBody ConsignorBaseInfoVo consignorBaseInfoVo, LoginEmployee loginEmployee) {
        customerInfoService.updateBaseInfo(consignorBaseInfoVo, loginEmployee);
    }

    /**
     * 淘汰客户
     *
     * @param elimination
     * @param loginEmployee
     */
    @RequestMapping(value = "eliminate", method = RequestMethod.POST)
    @ResponseBody
    public void insertEliminationRecord(@RequestBody ConsignorCustomerElimination elimination,
                                        LoginEmployee loginEmployee) {
        customerInfoService.eliminate(elimination, loginEmployee);
    }

    /**
     * 获取最后一条淘汰纪录
     *
     * @param crmId
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "{crmId}/EliminationInfo", method = RequestMethod.POST)
    @ResponseBody
    public ConsignorCustomerElimination findEliminationRecord(@PathVariable(value = "crmId") Integer crmId,
                                                              LoginEmployee loginEmployee) {
        return customerInfoService.findLastEliminationByCustomerId(crmId);
    }

    /**
     * 获取基础参数
     *
     * @param key
     * @return
     */
    @RequestMapping(value = "{key}/crmConfig", method = RequestMethod.GET)
    @ResponseBody
    public List<ConfParamOption> getConfigParams(@PathVariable(value = "key") String key) {
        return super.findConfig(key);
    }

    /**
     * 获取所有的区域信息
     *
     * @return
     */
    @RequestMapping(value = "allRegion", method = RequestMethod.POST)
    @ResponseBody
    public Object dumpAllRegion() {
        List<RegionVo> provinceList = new ArrayList<>();
        for (String code : regionCode) {
            // 获取省
            Region province = regionService.findByRegionCode(code);
            RegionVo pVo = new RegionVo();
            BeanUtils.copyProperties(province, pVo);
            provinceList.add(pVo);
            // 获取省下的市
            List<Region> cityList = regionService.findChildRegion(province.getRegionId());
            if (CollectionUtils.isEmpty(cityList)) {
                continue;
            }
            for (Region cr : cityList) {
                RegionVo cVo = new RegionVo();
                BeanUtils.copyProperties(cr, cVo);
                pVo.addChild(cVo);
                // 获取区县
                List<Region> districtList = regionService.findChildRegion(cr.getRegionId());
                if (CollectionUtils.isEmpty(districtList)) {
                    continue;
                }
                for (Region dr : districtList) {
                    RegionVo dVo = new RegionVo();
                    BeanUtils.copyProperties(dr, dVo);
                    cVo.addChild(dVo);
                }
            }

        }
        return provinceList;

    }

    /**
     * 通过crmCustomerId 获取crm联系人列表
     *
     * @param crmCustomerId
     * @return
     */
    @RequestMapping("/{crmCustomerId}/contactList")
    @ResponseBody
    public Object getContactByCustomerId(@PathVariable(value = "crmCustomerId") Integer crmCustomerId) {
        List<ConsignorContactsInfo> list = customerInfoService.findContactByCustomerId(crmCustomerId);
        return list;
    }

    /**
     * 通过企业客户id获取用车人列表
     *
     * @param customerInfoId
     * @return
     */
    @RequestMapping("{customerInfoId}/cargoOwnerList")
    @ResponseBody
    public List<TruckCustomer> findCargoOwnerByCustomerInfoId(
        @PathVariable(value = "customerInfoId") Integer customerInfoId) {
        return new ArrayList<>();
    }

    /**
     * 添加用车人
     *
     * @param cargoOwnerVo
     * @return
     */
    @RequestMapping(value = "addCargoOwner", method = RequestMethod.POST)
    @ResponseBody
    public CargoOwnerCustomerVo addCargoOwner(@RequestBody CargoOwnerVo cargoOwnerVo, LoginEmployee loginEmployee) {
        // 通过企业客户id获取数据
        if (cargoOwnerVo.getCustomerInfo() == null) {
            throw new BusinessException("customerInfoNull", "errors.paramCanNotNullWithName", "所属企业");
        }

        if (cargoOwnerVo.getCustomerInfo().getCustomerId() == null) {
            throw new BusinessException("customerInfoIdNull", "errors.paramCanNotNullWithName", "企业Id");
        }
        CustomerInfo customerInfo = customerInfoService.findCusInfoById(cargoOwnerVo.getCustomerInfo().getCustomerId());
        if (customerInfo == null) {
            throw new BusinessException("customerInfoNotFound", "errors.notFound");
        }
        cargoOwnerVo.setCustomerInfo(customerInfo);

        CargoOwnerCustomerVo vo = tgmUserCenterService.createCargoOwnerBelongEnterprise(cargoOwnerVo.getTruckCustomer(),
            cargoOwnerVo.getCustomerInfo(), loginEmployee);

        return vo;
    }

    /**
     * 获取子级渠道
     *
     * @param sourceCode
     * @return
     */
    @RequestMapping(value = "{sourceCode}/customerSource")
    @ResponseBody
    public List<Chanel> findChildrenByCode(@PathVariable(value = "sourceCode") String sourceCode) {
        return customerInfoService.findCustomerSourceByCode(sourceCode);
    }

    /**
     * 获取当前用户的所有业务区域
     *
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "getCurrentUserBusinessAreaList", method = RequestMethod.GET)
    @ResponseBody
    public Set<BusinessAreaNode> getCurrentUserBusinessAreaList(LoginEmployee loginEmployee) {
        List<BusinessAreaNode> areas = authorityService.findBusinessAreaTree(loginEmployee);
        Set<BusinessAreaNode> targetTree = new HashSet<>();
        this.parallelList(areas, targetTree);

        return targetTree;
    }

    /**
     * 运单概览
     *
     * @param pageCondition
     * @param loginEmployee
     * @return
     */
    @RequestMapping("waybillOverview")
    @ResponseBody
    public Map<String, Object> waybillOverview(@RequestBody PageCondition pageCondition, LoginEmployee loginEmployee) {
        // 派车中、待配送、配送中的运单列表
        this.buildQueryParam(pageCondition, loginEmployee);
        Map<String, Object> rst = new HashMap<>();

        Page<WaybillTrackOverviewBo> finalData = new Page<>(pageCondition.getPageNo(), pageCondition.getPageSize(), 0,
            null);
        Page<Waybill> datas = waybillService.searchWaybillBasicInfo(pageCondition);

        Collection<Waybill> dataList = datas.getResults();
        if (CollectionUtils.isEmpty(dataList)) {
            return rst;
        }

        List<WaybillTrackOverviewBo> finalList = new ArrayList<>();
        // 获取抢单时间
        long timeLength = this.getExpireTimeLength();
        Date lastFeedBackTime = null;
        WaybillTrackOverviewBo waybillTrackOverviewBo = null;
        // 组装不同状态展示的类容
        for (Waybill wb : dataList) {
            lastFeedBackTime = this.getLastFeedbackTime(wb);

            waybillTrackOverviewBo = new WaybillTrackOverviewBo(wb, timeLength, lastFeedBackTime);
            // 企业名称
            waybillTrackOverviewBo.setCustomerName(this.getCustomerName(wb));
            finalList.add(waybillTrackOverviewBo);
        }
        finalData.setResults(finalList);
        finalData.setTotal(datas.getTotal());

        rst.put("datas", finalData);
        rst.put("expireTimeLength", timeLength);
        rst.put("cDate", new Date());

        return rst;
    }

    /**
     * 获取企业客户名字
     *
     * @param waybill
     * @return
     */
    private String getCustomerName(Waybill waybill) {
        CustomerInfo customerInfo = customerInfoService.findCusInfoById(waybill.getCustomerId());

        if (customerInfo == null) {
            return null;
        }

        return customerInfo.getCustomerName();
    }

    /**
     * 获取最后派车返回记录
     *
     * @param waybill
     * @return
     */
    private Date getLastFeedbackTime(Waybill waybill) {
        // 获取订单派车反馈时间
        WaybillOperateTrack track = null;
        try {
            if (StringUtils.isBlank(waybill.getAssignCarFeedback())) {
                return null;
            }
            List<WaybillOperateTrack> trackList = waybillOperateTrackService.ListByWayBillIdAndOperateType(
                waybill.getWaybillId(), OperateType.ASSIGN_FEED_BACK.getCode());
            if (CollectionUtils.isEmpty(trackList)) {
                return null;
            }

            track = (WaybillOperateTrack) CollectionUtils.get(trackList, 0);
        } catch (Exception e) {
            logger.error("获取派车反馈时间错误.", e);
            return null;
        }
        return track.getCreateTime();
    }

    /**
     * 获取超时时长（毫秒）
     *
     * @return
     */
    private long getExpireTimeLength() {
        long expireTimeLength = 10 * 60 * 1000;

        List<ConfParamOption> optionList = confParamService.findParamOptions(Constants.COMPETE_BILL_KEY_EXPIRE_TIME);

        if (CollectionUtils.isNotEmpty(optionList)) {
            ConfParamOption option = optionList.get(0);
            if (StringUtils.isNumeric(option.getOptionValue())) {
                expireTimeLength = Long.parseLong(option.getOptionValue()) * 60 * 1000;
            }
        } else {
            logger.info("没有配置经纪人运单超时时间，使用默认值10分钟");
        }

        return expireTimeLength;
    }

    /**
     * 运单概览参数处理
     *
     * @param pageCondition
     * @param loginEmployee
     */
    private void buildQueryParam(PageCondition pageCondition, LoginEmployee loginEmployee) {
        Map<String, Object> params = pageCondition.getFilters();

        if (params == null) {
            params = new HashMap<>();
            pageCondition.setFilters(params);
        }
        // 设置客户经理id
        params.put("customerManagerId", loginEmployee.getEmployeeId());

        // 状态code转换
        List<Integer> codes = null;
        try {
            List<String> codeStr = (List<String>) params.get("statusViewList");
            codes = this.waybillStatusViewStr2Code(codeStr);
        } catch (Exception e) {
            logger.error("运单状态转换异常", e);
            return;
        }

        params.put("statusViewList", codes);

    }

    /**
     * 运单轨迹详情
     *
     * @param waybillId
     * @param loginEmployee
     * @return
     */
    @RequestMapping("{waybillId}/WaybillTrackDetail")
    @ResponseBody
    public WaybillTrackDetailBo getWaybillTrackDetail(@PathVariable("waybillId") Integer waybillId,
                                                      LoginEmployee loginEmployee) {
        // 获取运单详情
        Waybill waybill = waybillService.getWaybill(waybillId);
        // 组装数据
        long timeLength = this.getExpireTimeLength();
        WaybillTrackDetailBo waybillTrackDetailBo = waybillTrackDetailBuilder.buildTrackDetailBo(waybill,
            WaybillTrackDetailBuilder.allOperationList, timeLength);
        waybillTrackDetailBo.setCustomerName(this.getCustomerName(waybill));
        return waybillTrackDetailBo;
    }

    private String KEY_TODAY = "today";
    private String KEY_TOMORROW = "tomorrow";

    /**
     * 获取空闲运力
     *
     * @param date
     * @return
     */
    @RequestMapping(value = "{date}/transportCapacity", method = RequestMethod.POST)
    @ResponseBody
    public TransportCapacityBo getTransportCapacity(@PathVariable("date") String date, LoginEmployee loginEmployee) {
        TransportCapacityBo transportCapacityBo = new TransportCapacityBo();
        this.buildTimeParam(date, transportCapacityBo);

        return transportCapacityBo;
    }

    // 业绩
    @RequestMapping("performance")
    @ResponseBody
    public CustomerPerformanceVo getCustomerManPerformance(LoginEmployee loginEmployee) {
        return waybillService.getCustomerManPerformanceOverall(loginEmployee);
    }

    // 欠款统计
    @RequestMapping("customerManDebtOverall")
    @ResponseBody
    public CustomerManagerDebtVo getCustomerManDebtOverall(LoginEmployee loginEmployee) {
        return waybillService.getCustomerManDebtOverall(loginEmployee);
    }

    // 欠款详情
    @RequestMapping("customerManDebtDetail")
    @ResponseBody
    public CustomerManagerDebtOverviewVo getCustomerManDebtDetail(@RequestBody PageCondition pageCondition,
                                                                  LoginEmployee loginEmployee) {
        return waybillService.getSeparationDebt(pageCondition, loginEmployee);
    }

    // 客户欠款明细
    @RequestMapping("customerDebtDetail")
    @ResponseBody
    public CustomerManagerDebtDetailVo getCustomerDebtDetail(@RequestBody PageCondition pageCondition,
                                                             LoginEmployee loginEmployee) {
        return waybillService.getCustomerDebtDetail(pageCondition, loginEmployee);
    }

    /**
     * 出发城市列表
     */
    @ResponseBody
    @RequestMapping(value = "startCityList", method = RequestMethod.GET)
    public CityManageInfoVo startCityList() {
        CityManageInfo cityInfo = cityManageService.getCityList(CityManage.Sign.START_FROM.getCode());

        // 组装为二级结构
        return this.buildCityInfo(cityInfo.getCityManageList());

    }

    /**
     * 到达城市列表
     */
    @ResponseBody
    @RequestMapping(value = "endCityList", method = RequestMethod.GET)
    public CityManageInfoVo endCityList() {
        CityManageInfo cityInfo = cityManageService.getCityList(CityManage.Sign.END_FORM.getCode());

        // 组装为二级结构
        return this.buildCityInfo(cityInfo.getCityManageList());

    }

    /**
     * 落地配-客户管理
     *
     * @param pageCondition
     * @param type
     * @param loginEmployee
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "{type}/scatteredCustomerSearch", method = RequestMethod.POST)
    public Page<ScatteredCustomerVo> scatteredCustomerSearch(@RequestBody PageCondition pageCondition,
                                                             @PathVariable("type") Integer type, LoginEmployee loginEmployee) {
        // 列表类型
        ScatteredCustomerVo.CustomerType listType = ScatteredCustomerVo.CustomerType.getByCode(type);
        // 租户信息
        pageCondition.getFilters().put("customerManagerUserId", loginEmployee.getEmployeeId());
        Page<ScatteredCustomerVo> pageData = customerInfoService.scatteredCustomerSearch(pageCondition, listType);

        return pageData;
    }

    /**
     * 落地配 客户经理运单概览
     *
     * @param type
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "{type}/overview", method = RequestMethod.GET)
    public ScatteredCustomerVo.OverViewDataVo countOverViewData(@PathVariable("type") Integer type,
                                                                LoginEmployee loginEmployee) {
        ScatteredCustomerVo.CustomerType countType = ScatteredCustomerVo.CustomerType.getByCode(type);
        ScatteredCustomerVo.OverViewDataVo vo = customerInfoService.countOverViewData(loginEmployee.getEmployeeId(),
            countType);

        return vo;
    }

    /**
     * 落地配 crm联系人货主列表
     *
     * @param crmCustomerId
     * @param loginEmployee
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "scattered/{crmCustomerId}/cargoOwnerList")
    public List<TruckCustomer> findCargoOwnerFromCrm(@PathVariable("crmCustomerId") Integer crmCustomerId,
                                                     LoginEmployee loginEmployee) {
        List<TruckCustomer> list = customerInfoService.findCargoOwnerFromCrm(crmCustomerId, loginEmployee);

        return list;
    }

    /**
     * 落地配 重新下单
     *
     * @param waybillId
     * @param loginEmployee
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "scattered/waybill/{waybillId}/detail", method = RequestMethod.GET)
    public WaybillDetailInfo getScatteredBillDetail(@PathVariable("waybillId") Integer waybillId,
                                                    LoginEmployee loginEmployee) {
        WaybillDetailInfo detailInfo = scatteredWaybillService.getScatteredBillDetail(waybillId, loginEmployee);

        return detailInfo;
    }

    /**
     * 落地配-运单详情
     *
     * @param waybillId
     * @return
     */
    @ResponseBody
    @RequestMapping("scattered/waybill/{waybillId}/viewDetail")
    public ScatteredWaybillCargoOwnerDetailVo scatteredWaybillViewDetail(@PathVariable("waybillId") Integer waybillId,
                                                                         LoginEmployee loginEmployee) {
        ScatteredWaybillViewVo scatteredWaybillViewVo = scatteredWaybillService.getDetail(waybillId, loginEmployee);

        ScatteredWaybillCargoOwnerDetailVo detailVo = new ScatteredWaybillCargoOwnerDetailVo();
        detailVo.setScatteredWaybillViewVo(scatteredWaybillViewVo);

        return detailVo;
    }


    @ApiOperation(value = "承运商列表")
    /**
     * 当前租户下的承运商列表
     *
     * @param vendorName
     * @param pageSize
     * @param loginEmployee
     * @return
     */
    @ResponseBody
    @RequestMapping("vendorMappingList")
    public List<VendorMapping> findVendorMapping(String vendorName, Integer pageSize, LoginEmployee loginEmployee) {

        return vendorMappingService.listVendorMapping(vendorName, pageSize, loginEmployee);
    }



    @ApiOperation(value = "通过承运商关系id和货源方项目id获取承运方项目id")
    /**
     * 通过选择的承运商关系id和货源方项目id获取承运方项目
     * @param vendorMappingId
     * @param projectId
     * @return
     */
    @ResponseBody
    @RequestMapping("getCarrierProject/{vendorMappingId}/{projectId}")
    public Project getCarrierProject(@PathVariable("vendorMappingId") Integer vendorMappingId, @PathVariable("projectId") Integer projectId) {
        return vendorMappingService.getByMappingIdAndSourceProjectId(vendorMappingId, projectId);
    }


    @ApiOperation(value = "经纪人端车队搜索")
    /**
     * 搜索车队中的车辆
     * @param truckFleetPageQueryCondition
     * @param loginEmployee
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "searchTruckFleet", method = RequestMethod.POST)
    public Page<DriverTruckInfoBo> searchTruckFleet(@RequestBody TruckFleetPageQueryCondition truckFleetPageQueryCondition, LoginEmployee loginEmployee) {
        Page<DriverTruckInfoBo> pageData = customerManagerService.searchTruckFleet(truckFleetPageQueryCondition, loginEmployee);

        return pageData;
    }

    @ApiOperation(value = "查询客户物流产品标签")
    @ResponseBody
    @RequestMapping(value = "findLogisticsProduct/{customerId}", method = RequestMethod.GET)
    public List<LogisticsProductBo> findLogisticsProduct(@PathVariable("customerId") Integer customerId, LoginEmployee loginEmployee){
        CustomerInfo customerInfo = customerInfoService.findCusInfoById(customerId);
        if(customerInfo != null){
            return customerManagerService.findLogisticsProduct(customerInfo.getCrmCustomerId(), loginEmployee);
        }
        return null;
    }

    /**
     * 按省分组
     *
     * @param cities
     */
    public CityManageInfoVo buildCityInfo(List<CityManage> cities) {

        if (CollectionUtils.isEmpty(cities))
            return null;

        CityManageInfoVo vo = new CityManageInfoVo();
        for (CityManage city : cities) {
            vo.addCity(city);
        }

        return vo;
    }

    private void buildTimeParam(String dateName, TransportCapacityBo transportCapacityBo) {
        Date begin = null;
        Date end = null;
        Date now = new Date();
        if (StringUtils.equals(KEY_TODAY, dateName)) {
            begin = DateUtils.truncate(now, Calendar.MINUTE);
            end = DateUtils.setSeconds(DateUtils.setMinutes(DateUtils.setHours(begin, 23), 59), 59);
        } else if (StringUtils.equals(KEY_TOMORROW, dateName)) {
            begin = DateUtils.truncate(DateUtils.addDays(now, 1), Calendar.DATE);
            end = DateUtils.setSeconds(DateUtils.setMinutes(DateUtils.setHours(begin, 23), 59), 59);
        }

        if (begin == null)
            return;

        transportCapacityBo.setCountStartTime(begin);
        transportCapacityBo.setCountEndTime(end);
    }

    /**
     * 获取基础配名称
     *
     * @param configs
     * @param code
     * @return
     */
    private String filterConfName(Collection<ConfParamOption> configs, Object code) {
        if (CollectionUtils.isEmpty(configs)) {
            return null;
        }
        if (code == null) {
            return null;
        }
        for (ConfParamOption conf : configs) {
            if (StringUtils.equals(code.toString(), conf.getOptionValue())) {
                return conf.getOptionName();
            }
        }
        return null;
    }

    /**
     * 组装用于前端展示的企业客户信息
     *
     * @param consignorCustomerInfo
     * @return
     */
    private ConsignorCustomerInfoVo buildBasicInfoVo(ConsignorCustomerInfo consignorCustomerInfo,
                                                     LoginEmployee loginEmployee) {
        if (consignorCustomerInfo == null) {
            return null;
        }

        ConsignorCustomerInfoVo crmCustomerInfoVo = new ConsignorCustomerInfoVo();
        BeanUtils.copyProperties(consignorCustomerInfo, crmCustomerInfoVo);
        // 获取行业、客户性质、客户类型基础数据
        List<ConfParamOption> industryList = super.findConfig(this.CRM_OWNED_INDUSTRY);
        List<ConfParamOption> consignorTypeList = super.findConfig(this.CRM_CONSIGNOR_TYPE);
        List<ConfParamOption> enterpriseNatureList = super.findConfig(this.CRM_ENTERPRISE_NATURE);

        // 行业
        crmCustomerInfoVo.setOwnedIndustryStr(this.filterConfName(industryList, crmCustomerInfoVo.getOwnedIndustry()));
        // 性质
        crmCustomerInfoVo.setEnterpriseNatureStr(
            this.filterConfName(enterpriseNatureList, crmCustomerInfoVo.getEnterpriseNature()));
        // 类型
        crmCustomerInfoVo
            .setConsignorTypeStr(this.filterConfName(consignorTypeList, crmCustomerInfoVo.getConsignorType()));

        // 业务区域
        List<BusinessAreaNode> areas = authorityService.findBusinessAreaTree(loginEmployee);
        Set<BusinessAreaNode> targetTree = new HashSet<>();
        this.parallelList(areas, targetTree);
        crmCustomerInfoVo.setAreaCodeName(this.buildAreaCodeName(targetTree, crmCustomerInfoVo.getAreaCode()));

        // 客户来源
        Chanel chanel = customerInfoService.getChanelByCode(consignorCustomerInfo.getSourceChannelCode());
        if (chanel != null) {
            crmCustomerInfoVo.setSourceChannelCodeStr(chanel.getName());
        }

        // 获取淘汰纪录
        if (NumberUtils.compare(crmCustomerInfoVo.getStatus(),
            com.juma.crm.customer.domain.CustomerInfo.CustomerStatus.ELIMINATED.getValue()) == 0) {
            // 已淘汰客户，获取淘汰原因
            this.buildEliminationReason(crmCustomerInfoVo);
        }

        if (StringUtils.isBlank(crmCustomerInfoVo.getRegionCode())) {
            return crmCustomerInfoVo;
        }
        this.buildRegionInfoVo(crmCustomerInfoVo);

        return crmCustomerInfoVo;
    }

    /**
     * 组装业务区域
     *
     * @param areaNodes
     * @param code
     * @return
     */
    private String buildAreaCodeName(Collection<BusinessAreaNode> areaNodes, String code) {

        if (CollectionUtils.isEmpty(areaNodes)) {
            return null;
        }
        if (code == null) {
            return null;
        }

        for (BusinessAreaNode node : areaNodes) {
            if (StringUtils.equals(code.toString(), node.getAreaCode())) {
                return node.getAreaName();
            }
        }
        return null;

    }

    /**
     * 组装拜访记录vo
     *
     * @param consignorVisitRecords
     * @return
     */
    private List<ConsignorVisitRecordTgmVo> buildVisitRecodeVo(List<ConsignorVisitRecord> consignorVisitRecords) {

        if (CollectionUtils.isEmpty(consignorVisitRecords)) {
            return null;
        }

        List<ConsignorVisitRecordTgmVo> datas = new ArrayList<>();
        ConsignorVisitRecordTgmVo vo = null;
        for (ConsignorVisitRecord record : consignorVisitRecords) {
            vo = new ConsignorVisitRecordTgmVo();
            BeanUtils.copyProperties(record, vo);
//            List<ConfParamOption> vehicleDemandLevelList = super.findConfig(this.CRM_VEHICLE_DEMAND_LEVEL);
//            List<ConfParamOption> vehicleTypeList = super.findConfig(this.TRUCK_TYPE);
            List<ConfParamOption> visitWayList = super.findConfig(this.CRM_VISIT_WAY);

            // 需求程度
//            vo.setVehicleDemandLevelStr(this.filterConfName(vehicleDemandLevelList, vo.getVehicleDemandLevel()));
            // 车辆需求类型
//            vo.setVehicleDemandTypeStr(this.filterConfName(vehicleTypeList, vo.getVehicleDemandType()));
            // 拜访方式
            vo.setVisitWayStr(this.filterConfName(visitWayList, vo.getVisitWay()));
            // 拜访结果
//            vo.setEvaluationResultsStr(this.buildAppStatusName(vo.getEvaluationResults()));

            datas.add(vo);
        }
        return datas;
    }

    /**
     * app状态转换
     *
     * @param code
     * @return
     */
    private String buildAppStatusName(Byte code) {
        String statusName = null;
        if (NumberUtils.compare(code,
            com.juma.crm.customer.domain.CustomerInfo.CustomerStatus.UNDETERMINED.getValue()) == 0) {
            statusName = "待跟进";
        } else if (NumberUtils.compare(code,
            com.juma.crm.customer.domain.CustomerInfo.CustomerStatus.SIGN_SOON.getValue()) == 0) {
            statusName = "已成交";
        } else if (NumberUtils.compare(code,
            com.juma.crm.customer.domain.CustomerInfo.CustomerStatus.ELIMINATED.getValue()) == 0) {
            statusName = "已淘汰";
        }
        return statusName;
    }

    /**
     * 组装地址信息
     *
     * @param crmCustomerInfoVo
     * @return
     */
    private ConsignorCustomerInfoVo buildRegionInfoVo(ConsignorCustomerInfoVo crmCustomerInfoVo) {

        // 获取区域信息
        String regionCode = crmCustomerInfoVo.getRegionCode();

        Region region = regionService.findByRegionCode(regionCode);
        if (region == null) {
            return crmCustomerInfoVo;
        }
        List<Region> allRegions = null;
        try {
            allRegions = regionService.findAllLevelsRegion(region.getRegionId());
        } catch (CategoryCodeFormatException e) {
            logger.error(e.getMessage(), e);
        }
        for (Region reg : allRegions) {
            if (reg.getParentRegionId() == null) {
                crmCustomerInfoVo.setProvinceCode(reg.getRegionCode());
                crmCustomerInfoVo.setProvinceName(reg.getRegionName());
            } else if (reg.isLeaf()) {
                crmCustomerInfoVo.setDistrictCode(reg.getRegionCode());
                crmCustomerInfoVo.setDistrictName(reg.getRegionName());
            } else {
                crmCustomerInfoVo.setCityCode(reg.getRegionCode());
                crmCustomerInfoVo.setCityName(reg.getRegionName());
            }
        }

        return crmCustomerInfoVo;
    }

    /**
     * 枚举名称转code
     *
     * @param codeStr
     * @return
     */
    private List<Byte> crmCustomerStatusStr2Code(List<String> codeStr) {
        if (CollectionUtils.isEmpty(codeStr)) {
            return null;
        }

        List<Byte> statusList = new ArrayList<>();
        for (String str : codeStr) {
            statusList
                .add(Enum.valueOf(com.juma.crm.customer.domain.CustomerInfo.CustomerStatus.class, str).getValue());
        }
        return statusList;
    }

}
