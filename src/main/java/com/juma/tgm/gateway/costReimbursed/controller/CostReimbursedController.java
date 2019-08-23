package com.juma.tgm.gateway.costReimbursed.controller;

import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.user.domain.LoginEcoUser;
import com.juma.conf.domain.ConfParamOption;
import com.juma.conf.service.ConfParamService;
import com.juma.tgm.common.BaseUtil;
import com.juma.tgm.common.Constants;
import com.juma.tgm.costReimbursed.domain.CostReimbursed;
import com.juma.tgm.costReimbursed.domain.CostReimbursed.TimeParamList;
import com.juma.tgm.costReimbursed.service.CostReimbursedService;
import com.juma.tgm.gateway.costReimbursed.vo.CostReimbursedVO;
import com.juma.tgm.tools.service.VmsCommonService;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.service.WaybillService;
import com.juma.vms.driver.domain.Driver;
import com.juma.vms.driver.domain.DriverTenant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CostReimbursedController.java
 * @Description 费用报销
 * @author Libin.Wei
 * @Date 2017年7月11日 上午8:59:07
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("cost/reimbursed")
public class CostReimbursedController {

    @Resource
    private CostReimbursedService costReimbursedService;
    @Resource
    private ConfParamService confParamService;
    @Resource
    private WaybillService waybillService;
    @Resource
    private VmsCommonService vmsCommonService;

    /**
     * 分页查询
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<CostReimbursed> search(@RequestBody PageCondition pageCondition, LoginEcoUser driverLoginEcoUser) {
        Driver driver = vmsCommonService.loadDriverByUserId(driverLoginEcoUser.getUserId());
        if (null == driver) {
            return new Page<CostReimbursed>(pageCondition.getPageNo(), pageCondition.getPageSize(), 0,
                    new ArrayList<CostReimbursed>());
        }

        pageCondition.getFilters().put("costReimbursedKey", CostReimbursed.CostReimbursedKey.DRIVER_COST_REIMBURSED.toString());
        pageCondition.getFilters().put("driverId", driver.getDriverId());
        Map<String, Object> filters = pageCondition.getFilters();
        Object obj = filters.get("timeRecord");
        if (null != obj) {
            Map<String, Date> map = CostReimbursed.TimeParamList.getMapByCode(BaseUtil.strToNum(obj.toString()));
            filters.putAll(map);
        }
        return costReimbursedService.search(pageCondition, driverLoginEcoUser);
    }

    /**
     * 详情
     */
    @ResponseBody
    @RequestMapping(value = "{costReimbursedId}/detail", method = RequestMethod.GET)
    public CostReimbursed detail(@PathVariable Integer costReimbursedId, LoginEcoUser driverLoginEcoUser) {
        return costReimbursedService.findCostReimbursedAndUrlById(costReimbursedId);
    }

    /**
     * 添加
     */
    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public void add(@RequestBody CostReimbursed costReimbursed, LoginEcoUser driverLoginEcoUser) {
        costReimbursed.setCostReimbursedKey(CostReimbursed.CostReimbursedKey.DRIVER_COST_REIMBURSED.toString());

        Driver driver = vmsCommonService.loadDriverByUserId(driverLoginEcoUser.getUserId());
        if (null == driver) {
            throw new BusinessException("driverNotfound", "driver.error.not.found");
        }

        DriverTenant driverTenant = vmsCommonService.loadDriverTenantByDriverId(driver.getDriverId(), driverLoginEcoUser);
        if (null == driverTenant) {
            throw new BusinessException("driverNotfound", "driver.error.not.found");
        }
        costReimbursed.setAreaCode(driverTenant.getAreaCode());
        costReimbursed.setTenantCode(driverTenant.getTenantCode());
        costReimbursedService.insert(costReimbursed, driverLoginEcoUser);
        costReimbursedService.recountThePrice(costReimbursed.getWaybillId(), driverLoginEcoUser);
    }

    /**
     * 批量添加
     */
    @ResponseBody
    @RequestMapping(value = "batch/add", method = RequestMethod.POST)
    public void batchAdd(@RequestBody CostReimbursedVO costReimbursedVO, LoginEcoUser driverLoginEcoUser) {
        Driver driver = vmsCommonService.loadDriverByUserId(driverLoginEcoUser.getUserId());
        if (null == driver) {
            throw new BusinessException("driverNotfound", "driver.error.not.found");
        }

        DriverTenant driverTenant = vmsCommonService.loadDriverTenantByDriverId(driver.getDriverId(), driverLoginEcoUser);
        if (null == driverTenant) {
            throw new BusinessException("driverNotfound", "driver.error.not.found");
        }

        List<CostReimbursed> listCostReimbursed = costReimbursedVO.getListCostReimbursed();
        for (CostReimbursed costReimbursed : listCostReimbursed) {
            costReimbursed.setTenantCode(driverTenant.getTenantCode());
            costReimbursed.setAreaCode(driverTenant.getAreaCode());
            costReimbursed.setWaybillId(costReimbursedVO.getWaybillId());
        }
        costReimbursedService.insertBatch(listCostReimbursed, driverLoginEcoUser);
        
        Waybill waybill = waybillService.getWaybill(costReimbursedVO.getWaybillId());
        if(waybill != null && waybill.getTenantId() == 2) {
            costReimbursedService.recountThePrice(costReimbursedVO.getWaybillId(), driverLoginEcoUser);
        }
    }

    /**
     * 逻辑删除
     */
    @ResponseBody
    @RequestMapping(value = "{costReimbursedId}/delete", method = RequestMethod.POST)
    public void delete(@PathVariable Integer costReimbursedId, LoginEcoUser driverLoginEcoUser) {
        CostReimbursed costReimbursed = costReimbursedService.getCostReimbursed(costReimbursedId);
        if (null != costReimbursed) {
            costReimbursed.setIsDelete(true);
            costReimbursedService.update(costReimbursed, driverLoginEcoUser);
        }
    }

    /**
     * 费用类型列表
     */
    @ResponseBody
    @RequestMapping(value = "type/list", method = RequestMethod.GET)
    public List<ConfParamOption> CostReimbursedTypeList() {
        List<ConfParamOption> list = confParamService.findParamOptions(Constants.COST_REIMBURSED_TYPE);
        if (null == list) {
            return new ArrayList<ConfParamOption>();
        }
        return list;
    }

    /**
     * 费用报销时间段选择
     */
    @ResponseBody
    @RequestMapping(value = "time/param/list", method = RequestMethod.GET)
    public Object timeParamList() {
        List<ConfParamOption> result = new ArrayList<ConfParamOption>();
        for (TimeParamList timeParamList : CostReimbursed.TimeParamList.values()) {
            ConfParamOption option = new ConfParamOption();
            option.setOptionName(timeParamList.getDesc());
            option.setOptionValue(timeParamList.getCode() + "");
            result.add(option);
        }
        return result;
    }
}
