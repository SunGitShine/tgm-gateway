package com.juma.tgm.gateway.customer.controller;

import com.alibaba.fastjson.JSONObject;
import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.crm.domain.CustomerInfo;
import com.juma.tgm.crm.service.CustomerInfoService;
import com.juma.tgm.customer.service.CustomerManagerService;
import com.juma.tgm.customerManager.domain.FixedDemand;
import com.juma.tgm.customerManager.domain.FixedDemandDeliveryPoint;
import com.juma.tgm.customerManager.domain.vo.TruckFleetParamVo;
import com.juma.tgm.customerManager.service.FixedDemandService;
import com.juma.tgm.customerManager.service.vo.FixedDemandVo;
import com.juma.tgm.gateway.common.BaseController;
import com.juma.tgm.truck.domain.bo.DriverTruckInfoBo;
import com.juma.tgm.waybill.domain.TruckRequire;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillDeliveryAddress;
import com.juma.tgm.waybill.domain.WaybillDetailInfo;
import com.juma.tgm.waybill.domain.WaybillParam;
import com.juma.tgm.waybill.domain.WaybillReceiveAddress;
import com.juma.tgm.waybill.service.TruckRequireService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: FixedDemandController
 * @Description:
 * @author: liang
 * @date: 2017-07-25 10:48
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
@Controller
@RequestMapping("/fixedDemand")
public class FixedDemandController extends BaseController {


    @Resource
    private FixedDemandService fixedDemandService;

    @Resource
    private TruckRequireService truckRequireService;

    @Resource
    private CustomerInfoService customerInfoService;

    @Resource
    private CustomerManagerService customerManagerService;

    //列表页
    @RequestMapping(value = "page", method = RequestMethod.POST)
    @ResponseBody
    public Page<FixedDemandVo> getPage(@RequestBody PageCondition pageCondition, LoginEmployee loginEmployee) {

        Map<String, Object> param = pageCondition.getFilters();
        if (param == null) {
            param = new HashMap<>();
            pageCondition.setFilters(param);
        }
        param.put("customerManagerId", loginEmployee.getEmployeeId());
        //支持客户和用车人搜索
        this.buildQueryParam(pageCondition);

        Page<FixedDemandVo> pageData = fixedDemandService.getFixedDemandList(loginEmployee, pageCondition);

        if (CollectionUtils.isNotEmpty(pageData.getResults())) {
            for (FixedDemandVo vo : pageData.getResults()) {
                //运单基础信息
                this.buildFixedDemandViewData(vo);
            }
        }

        return pageData;
    }

    /**
     * 转换参数
     *
     * @param condition
     */
    private void buildQueryParam(PageCondition condition) {
        Map<String, Object> param = condition.getFilters();
        if (MapUtils.isEmpty(param)) {
            return;
        }

        //以下条件必须同时满足
        String type = null;
        try {
            type = param.get("type").toString();
        } catch (Exception e) {
            return;
        } finally {
            param.remove("type");
        }
        //判断参数是企业客户
        String custStr = null;
        try {
            custStr = param.get("customerId").toString();
        } catch (Exception e) {
            return;
        } finally {
            param.remove("customerId");
        }
        
        if (StringUtils.isNumeric(custStr) && StringUtils.equals("1", type)) {
            param.put("customerId", Integer.valueOf(custStr));
        } else if (StringUtils.isNumeric(custStr) && StringUtils.equals("2", type)) {
            param.put("truckCustomerId", Integer.valueOf(custStr));
        } else if (StringUtils.isNumeric(custStr) && StringUtils.equals("3", type)) {
            param.put("projectId", Integer.valueOf(custStr));
        }
    }

    /**
     * 组装运单基础信息
     *
     * @param demandVo
     */
    private void buildFixedDemandViewData(FixedDemandVo demandVo) {

        if (demandVo == null) return;
        if (demandVo.getFixedDemand() == null) return;
    }

    /**
     * 通车要求
     *
     * @param truckRequire
     * @return
     */
    private String getRequireStr(TruckRequire truckRequire) {
        if (truckRequire == null) return "";

        StringBuffer sb = new StringBuffer();
        String requireStr = truckRequireService.getTruckRequireString(truckRequire, sb);
        return requireStr;
    }

    //货物信息
    private String getGoodInfoStr(TruckRequire truckRequire) {
        if (truckRequire == null) return "";

        StringBuffer sb = new StringBuffer();
        //货物信息
        if (StringUtils.isNotBlank(truckRequire.getGoodsType())) {
            sb.append(" | ");
            sb.append(truckRequire.getGoodsType());
        }
        if (StringUtils.isNotBlank(truckRequire.getGoodsWeight())) {
            sb.append(" | ");
            sb.append(truckRequire.getGoodsWeight() + "吨");
        }

        if (StringUtils.isNotBlank(truckRequire.getGoodsVolume())) {
            sb.append(" | ");
            sb.append(truckRequire.getGoodsVolume() + "方");
        }
        String truckRequireStr = sb.toString();
        if (StringUtils.isNotBlank(truckRequireStr)) {
            truckRequireStr = truckRequireStr.trim();
            if (truckRequireStr.startsWith("| ")) {
                truckRequireStr = truckRequireStr.substring(1, truckRequireStr.length()).trim();
            }
        }
        return truckRequireStr;
    }

    /**
     * 新增
     *
     * @param demandVo
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "add")
    @ResponseBody
    public Integer add(@RequestBody FixedDemandVo demandVo, LoginEmployee loginEmployee) {
        try {
            demandVo.setRequireStr(JSONObject.toJSONString(demandVo.getTruckRequire()));
        } catch (Exception e) {
            throw new BusinessException("TruckRequireError", "errors.paramError");
        }
        FixedDemand demand = demandVo.getFixedDemand();
        demand.setRequireJson(demandVo.getRequireStr());
        demand.setCustomerManagerId(loginEmployee.getEmployeeId());
        return fixedDemandService.add(demandVo, loginEmployee);
    }

    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "{id}/del")
    @ResponseBody
    public void del(@PathVariable(value = "id") Integer id, LoginEmployee loginEmployee) {
        fixedDemandService.del(id);
    }

    /**
     * 编辑
     *
     * @param demandVo
     * @param loginEmployee
     */
    @RequestMapping(value = "update")
    @ResponseBody
    public void update(@RequestBody FixedDemandVo demandVo, LoginEmployee loginEmployee) {
        try {
            demandVo.setRequireStr(JSONObject.toJSONString(demandVo.getTruckRequire()));
        } catch (Exception e) {
            throw new BusinessException("TruckRequireError", "errors.paramError");
        }
        FixedDemand demand = demandVo.getFixedDemand();
        demand.setRequireJson(demandVo.getRequireStr());
        fixedDemandService.update(demandVo, loginEmployee);
    }

    /**
     * 获取详情
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}/detail")
    @ResponseBody
    public WaybillDetailInfo getDetail(@PathVariable(value = "id") Integer id) {

        WaybillDetailInfo detail = new WaybillDetailInfo();

        FixedDemandVo baseVo = fixedDemandService.get(id);

        if (baseVo == null) return detail;

        this.buildFixedDemandViewData(baseVo);

        //用车人
        detail.setTruckCustomerForUserCar(baseVo.getTruckCustomer());
        //企业客户
        FixedDemand fixedDemand = baseVo.getFixedDemand();
        if (fixedDemand.getCustomerId() != null) {
            CustomerInfo info = customerInfoService.findCusInfoById(fixedDemand.getCustomerId());
            detail.setCustomerInfo(info);
        }
        //项目计价
        
        WaybillParam waybillParam = new WaybillParam();
        waybillParam.setProjectFreightRuleJson(fixedDemand.getProjectFreightRuleJson());
        waybillParam.setRequiredMinTemperature(fixedDemand.getRequiredMinTemperature());
        waybillParam.setRequiredMaxTemperature(fixedDemand.getRequiredMaxTemperature());
        detail.setWaybillParam(waybillParam);
        //用车要求
        TruckRequire truckRequire = null;
        try {
            truckRequire = JSONObject.parseObject(fixedDemand.getRequireJson(), TruckRequire.class);

        } catch (Exception e) {
        }
        detail.setTruckRequire(truckRequire);
        //收货地
        detail.setWaybillReceiveAddresses(this.transformToWaybillReceiveAddrs(baseVo.getReceiveAddresses()));
        // 取货地
        detail.setWaybillDeliveryAddresses(this.transformToWaybillDeliveryAddrs(baseVo.getDeliveryAddresses()));
        //运单信息
        detail.setWaybill(this.transformToWaybill(fixedDemand));
        detail.setVehicleCount(fixedDemand.getVehicleCount());
        detail.setDeliveryTimePoint(fixedDemand.getDeliveryTimePoint());
        detail.setFinishTimePoint(fixedDemand.getFinishTimePoint());
        detail.setTruckRequireStr(this.getRequireStr(truckRequire));
        detail.setGoodsInfoStr(this.getGoodInfoStr(truckRequire));
        detail.setCanUseCustomerInfo(customerInfoService.customerBelongToManager(fixedDemand.getCustomerId(), fixedDemand.getCustomerManagerId()));
        detail.setBillStrategy(baseVo.billStrategyToObject());
        detail.setIsAutoCreateBill(fixedDemand.getIsAutoCreateBill());
        //固定车辆
        detail.setFixedDemandTrucks(baseVo.getFixedDemandTruck());

        return detail;
    }


    /**
     * 重做司机结算价
     */
    @RequestMapping(value = "do4DriverFreight", method = RequestMethod.POST)
    @ResponseBody
    public void add4DriverFreight() {
        fixedDemandService.add4DriverFreightBatch();
    }

    /**
     * 运单配送地
     *
     * @param deliveryPoints
     * @return
     */
    private List<WaybillDeliveryAddress> transformToWaybillDeliveryAddrs(List<FixedDemandDeliveryPoint> deliveryPoints) {
        List<WaybillDeliveryAddress> deliveryAddresses = new ArrayList<>();

        if (CollectionUtils.isEmpty(deliveryPoints)) return deliveryAddresses;

        WaybillDeliveryAddress wayBillAddrs = null;
        for (FixedDemandDeliveryPoint point : deliveryPoints) {
            wayBillAddrs = new WaybillDeliveryAddress();
            BeanUtils.copyProperties(point, wayBillAddrs);

            deliveryAddresses.add(wayBillAddrs);
        }

        return deliveryAddresses;
    }

    /**
     * 运单收货地
     *
     * @param receivePoints
     * @return
     */
    private List<WaybillReceiveAddress> transformToWaybillReceiveAddrs(List<FixedDemandDeliveryPoint> receivePoints) {
        List<WaybillReceiveAddress> receiveAddresses = new ArrayList<>();

        if (CollectionUtils.isEmpty(receivePoints)) return receiveAddresses;

        WaybillReceiveAddress wayBillAddrs = null;
        for (FixedDemandDeliveryPoint point : receivePoints) {
            wayBillAddrs = new WaybillReceiveAddress();
            BeanUtils.copyProperties(point, wayBillAddrs);

            receiveAddresses.add(wayBillAddrs);
        }

        return receiveAddresses;
    }


    /**
     * 固定需求转运单信息
     *
     * @param fixedDemand
     * @return
     */
    private Waybill transformToWaybill(FixedDemand fixedDemand) {
        Waybill waybill = new Waybill();

        waybill.setEstimateFreight(fixedDemand.getEstimateFreight());
        waybill.setShow4DriverFreight(fixedDemand.getShow4DriverFreight());
        waybill.setCustomerId(fixedDemand.getCustomerId());
        waybill.setTruckCustomerId(fixedDemand.getTruckCustomerId());
        waybill.setCustomerManagerId(fixedDemand.getCustomerManagerId());
        waybill.setReceiptType(fixedDemand.getReceiptType());
        waybill.setWaybillRemark(fixedDemand.getRemark());
        waybill.setNeedDeliveryPointNote(fixedDemand.getNeedDeliveryPointNote());
        waybill.setProjectId(fixedDemand.getProjectId());
        waybill.setBusinessBranch(fixedDemand.getBusinessBranch());
        waybill.setReceiveWay(fixedDemand.getReceiveWay());
        waybill.setOnlyLoadCargo(fixedDemand.getOnlyLoadCargo());
        waybill.setWaybillRemark(fixedDemand.getRemark());

        return waybill;
    }

    /**
     * 获取经纪人车队信息
     * @param truckFleetParamVo
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "truckFleet", method = RequestMethod.POST)
    @ResponseBody
    public List<DriverTruckInfoBo> findTruckFleet(@RequestBody TruckFleetParamVo truckFleetParamVo, LoginEmployee loginEmployee) {
        return customerManagerService.findTruckFleetForFixedDemand(truckFleetParamVo, loginEmployee);
    }

}
