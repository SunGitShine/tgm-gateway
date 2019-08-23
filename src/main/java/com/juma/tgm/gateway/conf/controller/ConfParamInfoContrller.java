package com.juma.tgm.gateway.conf.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.user.domain.LoginEcoUser;
import com.juma.auth.user.domain.LoginUser;
import com.juma.conf.domain.ConfParamOption;
import com.juma.conf.service.ConfParamService;
import com.juma.tgm.base.domain.BaseEnumDomian;
import com.juma.tgm.basicTruckType.service.BasicTruckTypeService;
import com.juma.tgm.basicTruckType.service.ConfParamInfoService;
import com.juma.tgm.cityManage.domain.CityManage;
import com.juma.tgm.configure.domain.ServiceConf;
import com.juma.tgm.configure.service.ServiceConfService;
import com.juma.tgm.gateway.common.JsonFieldCustomized;
import com.juma.tgm.gateway.waybill.controller.vo.CityManageInfoVo;
import com.juma.tgm.truck.domain.AdditionalFunction;
import com.juma.tgm.truck.domain.TruckType;
import com.juma.tgm.truck.service.AdditionalFunctionService;
import com.juma.tgm.truck.service.TruckTypeService;
import com.juma.tgm.waybill.domain.ConfParamInfo;
import com.juma.tgm.waybill.domain.TaxRate;
import com.juma.tgm.waybill.domain.TruckRequireInfo;
import com.juma.tgm.waybill.service.TaxRateService;

/**
 * 数据字典数据
 *
 * @author weilibin
 */

@Controller
@RequestMapping("conf")
public class ConfParamInfoContrller {

    private static final Logger logger = LoggerFactory.getLogger(ConfParamInfoContrller.class);

    @Resource
    private ConfParamInfoService confParamInfoService;

    @Resource
    private ConfParamService confParamService;

    @Resource
    private BasicTruckTypeService basicTruckTypeService;

    @Resource
    private TruckTypeService truckTypeService;

    @Resource
    private AdditionalFunctionService additionalFunctionService;

    @Resource
    private TaxRateService taxRateService;

    @Resource
    private ServiceConfService serviceConfService;

    /**
     * 落地配税率
     */
    private final static BigDecimal SCATTERED_TAX_RATE = new BigDecimal("0.11");


    @ResponseBody
    @RequestMapping(value = "goodsTypeList", method = RequestMethod.GET)
    @JsonFieldCustomized(includes = {"optionList", "optionName"})
    public ConfParamInfo goodsTypeList() {
        return confParamInfoService.goodsTypeList();
    }

    /**
     * 获取用户中新配置信息
     *
     * @param key
     * @return
     */
    @RequestMapping(value = "{key}/c", method = RequestMethod.GET)
    @ResponseBody
    public List<ConfParamOption> findConfig(@PathVariable(value = "key") String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return confParamService.findParamOptions(key);
    }

    /**
     * 员工使用：下单页获取货物类型，用车要求包装接口 加速客户端渲染过程
     *
     * @return
     */
    @RequestMapping(value = "employee/waybillBaseConf", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> waybillBaseConf(LoginEmployee loginEmployee) {
        Map<String, Object> rst = new HashMap<>();

        ConfParamInfo goodsTypeList = confParamInfoService.goodsTypeList();
        rst.put("goodsTypeList", goodsTypeList);

        TruckRequireInfo truckRequireInfo = basicTruckTypeService
                .getBasicTruckRequireInfo(loginEmployee.getTenantId(), null);

        rst.put("truckRequireInfo", truckRequireInfo);

        return rst;
    }

    /**
     * 生态用户使用：下单页获取货物类型，用车要求包装接口 加速客户端渲染过程
     *
     * @return
     */
    @RequestMapping(value = "eco/waybillBaseConf", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> waybillBaseConf(LoginEcoUser cargoOwnerLoginEcoUser) {
        Map<String, Object> rst = new HashMap<>();

        ConfParamInfo goodsTypeList = confParamInfoService.goodsTypeList();
        rst.put("goodsTypeList", goodsTypeList);

        TruckRequireInfo truckRequireInfo = basicTruckTypeService
                .getBasicTruckRequireInfo(cargoOwnerLoginEcoUser.getTenantId(), null);

        rst.put("truckRequireInfo", truckRequireInfo);

        return rst;
    }

    /**
     * 货主
     * 按用户租户和地区（选填）获取车型信息
     *
     * @param regionCode
     * @param cargoOwnerLoginEcoUser
     * @return
     */
    @RequestMapping(value = "eco/truckTypeCity/{regionCode}", method = RequestMethod.GET)
    @ResponseBody
    public List<TruckType> getTruckTypeCityForCargoOwner(@PathVariable(value = "regionCode") String regionCode, LoginEcoUser cargoOwnerLoginEcoUser) {
        List<TruckType> truckTypeCities = truckTypeService.listByRegionCode(regionCode, true, cargoOwnerLoginEcoUser);
        return truckTypeCities;
    }

    /**
     * 经纪人
     * 按用户租户和地区（选填）获取车型信息
     *
     * @param regionCode
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "manager/truckTypeCity/{regionCode}", method = RequestMethod.GET)
    @ResponseBody
    public List<TruckType> getTruckTypeCityForManager(@PathVariable(value = "regionCode") String regionCode, LoginEmployee loginEmployee) {
        List<TruckType> truckTypeCities = truckTypeService.listByRegionCode(regionCode, true, loginEmployee);
        return truckTypeCities;
    }


    /**
     * 落地配-用车要求-货主端
     *
     * @return
     */
    @RequestMapping(value = "eco/scattered/require", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getScatteredRequireInfo(LoginEcoUser cargoOwnerLoginEcoUser) {
        return this.buildScatteredRequireInfo(cargoOwnerLoginEcoUser);
    }

    /**
     * 落地配-用车要求-客户经理端
     *
     * @return
     */
    @RequestMapping(value = "scattered/require", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getScatteredRequireInfo(LoginEmployee loginEmployee) {
        return this.buildScatteredRequireInfo(loginEmployee);
    }

    // 落地配-用车要求
    private Map<String, Object> buildScatteredRequireInfo(LoginUser loginUser) {
        Map<String, Object> rst = new HashMap<>();
        List<AdditionalFunction> allFunctions = additionalFunctionService.getAllAdditionalFunction();
        if (CollectionUtils.isEmpty(allFunctions)) return rst;

        String[] targetFuns = new String[]{AdditionalFunction.FunctionKeys.COLLECTION_PAYMENT.name(), AdditionalFunction.FunctionKeys.NEED_RECEIPT.name(),
                AdditionalFunction.FunctionKeys.NEED_BACK_STORAGE.name(), AdditionalFunction.FunctionKeys.CARRY.name()};
        Arrays.sort(targetFuns);
        List<AdditionalFunction> destFunctions = new ArrayList<>();
        //代收货款
        //回单
        //返仓
        //搬运
        for (AdditionalFunction bo : allFunctions) {
            if (Arrays.binarySearch(targetFuns, bo.getFunctionKey()) >= 0) {
                destFunctions.add(bo);
            }
        }
        rst.put("deliveryRequire", destFunctions);

        //开票 落地配只有 11%的一种税率
        //税率
        List<TaxRate> taxRates = taxRateService.loadByTenant(loginUser);
        Set<TaxRate> finalTaxRates = new HashSet<>();
        rst.put("TaxRate", finalTaxRates);
        if (CollectionUtils.isEmpty(taxRates)) return rst;

        for (TaxRate tr : taxRates) {
            if (SCATTERED_TAX_RATE.equals(tr.getTaxRateValue())) {
                finalTaxRates.add(tr);
                break;
            }
        }


        return rst;
    }

    /**
     * 落地配已开通城市列表
     * 客户经理
     *
     * @param loginEmployee
     * @return
     */
    @RequestMapping("manager/runningCity")
    @ResponseBody
    public CityManageInfoVo getScatteredRunningCityForManager(LoginEmployee loginEmployee) {
        return this.getScatteredRunningCity(loginEmployee);
    }

    /**
     * 落地配已开通城市列表
     *
     * @param cargoOwnerLoginEcoUser
     * @return
     */
    @RequestMapping("cargoOwner/runningCity")
    @ResponseBody
    public CityManageInfoVo getScatteredRunningCityForCargoOwner(LoginEcoUser cargoOwnerLoginEcoUser) {
        return this.getScatteredRunningCity(cargoOwnerLoginEcoUser);
    }


    //落地配已开通城市列表
    private CityManageInfoVo getScatteredRunningCity(LoginUser loginUser) {
        List<ServiceConf> confs = serviceConfService.listServiceConf(loginUser);
        CityManageInfoVo infoVo = new CityManageInfoVo();
        if (CollectionUtils.isEmpty(confs)) return infoVo;

        CityManage cityManage = null;
        for (ServiceConf conf : confs) {
            cityManage = new CityManage();
            cityManage.setCityName(conf.getRegionName());
            cityManage.setCityCode(conf.getRegionCode());
            cityManage.setProvinceName(conf.getParentRegionName());
            cityManage.setProvinceCode(conf.getParentRegionCode());

            infoVo.addCity(cityManage);
        }

        return infoVo;
    }


    /**
     * (经纪人)获取当前城市已开通的箱型
     * @param regionCode
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "manager/scattered/vehicleBoxType/{regionCode}", method = RequestMethod.GET)
    @ResponseBody
    public Set<Map<String, String>> findVehicleBoxTypeForManager(@PathVariable("regionCode") String regionCode, LoginEmployee loginEmployee) {
        return this.doFindVehicleBoxTypeByLoginUser(regionCode, loginEmployee);
    }

    /**
     * (货主)获取当前城市已开通的箱型
     * @param regionCode
     * @param cargoOwnerLoginEcoUser
     * @return
     */
    @RequestMapping(value = "eco/scattered/vehicleBoxType/{regionCode}", method = RequestMethod.GET)
    @ResponseBody
    public Set<Map<String, String>> findVehicleBoxTypeForEco(@PathVariable("regionCode") String regionCode, LoginEcoUser cargoOwnerLoginEcoUser) {
        return this.doFindVehicleBoxTypeByLoginUser(regionCode, cargoOwnerLoginEcoUser);
    }

    /**
     * 获取当前可用的箱型
     *
     * @param loginUser
     * @param regionCode
     * @return
     */
    private Set<Map<String, String>> doFindVehicleBoxTypeByLoginUser(String regionCode, LoginUser loginUser) {
        Set<Map<String, String>> rst = new HashSet<>();
        List<TruckType> truckTypeList = truckTypeService.listByRegionCode(regionCode, true, loginUser);

        if (CollectionUtils.isEmpty(truckTypeList)) return rst;

        Map<Integer, String> boxNameMap = this.doGetVehicleBoxTypes();
        Map<String, String> tmpMap = null;
        for (TruckType tt : truckTypeList) {
            tmpMap = new HashMap<>();
            String vehicleBoxName = boxNameMap.get(tt.getVehicleBoxType());
            if (StringUtils.isBlank(vehicleBoxName)) continue;

            tmpMap.put("id", tt.getVehicleBoxType() + "");
            tmpMap.put("name", vehicleBoxName);

            rst.add(tmpMap);
        }

        return rst;
    }

    //箱型列表
    private Map<Integer, String> doGetVehicleBoxTypes() {
        List<BaseEnumDomian> listVehicleBoxType = truckTypeService.listVehicleBoxType();

        Map<Integer, String> boxNameMap = new HashMap<>();
        for (BaseEnumDomian domian : listVehicleBoxType) {
            try {
                boxNameMap.put(domian.getCode(), domian.getDesc());
            } catch (Exception e) {
                logger.error("箱型参数配置错误", e);
                continue;
            }
        }

        return boxNameMap;
    }

}
