package com.juma.tgm.gateway.truck.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.tools.PageCondition;
import com.juma.auth.authority.service.AuthorityService;
import com.juma.auth.conf.domain.BusinessAreaNode;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.user.domain.LoginEcoUser;
import com.juma.server.vm.service.vehicle.AmsServiceV2;
import com.juma.tgm.basicTruckType.service.BasicTruckTypeService;
import com.juma.tgm.common.Constants;
import com.juma.tgm.common.DateUtil;
import com.juma.tgm.driver.domain.Driver;
import com.juma.tgm.driver.domain.DriverLoginUser;
import com.juma.tgm.driver.service.DriverService;
import com.juma.tgm.gateway.common.AbstractController;
import com.juma.tgm.truck.domain.AdditionalFunction;
import com.juma.tgm.truck.domain.TruckType;
import com.juma.tgm.truck.domain.TruckVehicle;
import com.juma.tgm.truck.service.AdditionalFunctionService;
import com.juma.tgm.truck.service.TruckService;
import com.juma.tgm.truck.service.TruckTypeService;
import com.juma.tgm.waybill.domain.TruckRequireInfo;

/**
 * @author RX
 */
@Controller
@RequestMapping(value = "truck")
public class TruckController extends AbstractController {

    @Autowired
    private TruckService truckService;
    @Resource
    private DriverService driverService;
    @Resource
    private AmsServiceV2 amsServiceV2;
    @Resource
    private AuthorityService authorityService;
    @Autowired
    private TruckTypeService truckTypeService;
    @Autowired
    private BasicTruckTypeService basicTruckTypeService;
    @Autowired
    private AdditionalFunctionService additionalFunctionService;

    // TODO 2.6.0重构，方法暂时保留，但是新的接口移至driverController update by weilibin
    // 2017-05-20 15:01
    @Deprecated
    @RequestMapping(value = "{accept}/acceptAllocateOrder", method = RequestMethod.POST)
    @ResponseBody
    public void updateAcceptAllocateOrder(@PathVariable Integer accept, DriverLoginUser driverLoginUser,
                                          LoginEcoUser driverLoginEcoUser) {
        Driver driver = driverService.findDriverByUserId(driverLoginEcoUser.getUserId());
        if (null == driver) {
            return;
        }

        driver.setWhetherAcceptAllocateOrder(accept == 0 ? 0 : 1);
        driverService.updateDriverWhetherAcceptAllocateOrder(driver, driverLoginEcoUser);
    }

    /**
     * 客户经理首页：可用的货车数
     */
    @RequestMapping(value = "findAvailableTruckNo", method = RequestMethod.GET)
    @ResponseBody
    public Integer findTruckNoByDepartment(LoginEmployee loginEmployee) {
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNo(1);
        pageCondition.setPageSize(1);
        pageCondition.getFilters().put("startTime", DateUtil.dayAddStart(0));
        pageCondition.getFilters().put("endTime", DateUtil.dayAddEnd(0));
        // 区域
        List<BusinessAreaNode> areaNodeTree = authorityService.findBusinessAreaTree(loginEmployee);
        Set<BusinessAreaNode> targetTree = new HashSet<>();
        this.parallelList(areaNodeTree, targetTree);
        Set<String> areaNodeSet = new HashSet<String>();
        for (BusinessAreaNode areaNode : targetTree) {
            areaNodeSet.add(areaNode.getAreaCode());
        }
        pageCondition.getFilters().put("areaCodeLikeList", new ArrayList<String>(areaNodeSet));
        pageCondition.getFilters().put("tenantId", loginEmployee.getTenantId());

        int total = amsServiceV2.countAvailableFlightByPage(pageCondition);
        return total * 3 + Constants.TRUCK_RESIDUE;
    }

    /**
     * 得到所有的车型信息
     *
     * @return
     */
    @RequestMapping(value = "getALLTruckTypes", method = RequestMethod.GET)
    @ResponseBody
    public List<TruckType> getALLTruckTypes(LoginEmployee loginEmployee) {
        return truckTypeService.listAllTruckTypeSimpleByOrderNoAsc(loginEmployee.getTenantId(), null);
    }

    /**
     * 根据车型ID得到该车型所有的附加功能
     *
     * @param truckTypeId
     * @return
     */
    @RequestMapping(value = "getAdditionalFunctionByTruckTypeId/{truckTypeId}", method = RequestMethod.GET)
    @ResponseBody
    public List<AdditionalFunction> getAdditionalFunctionByTruckTypeId(@PathVariable Integer truckTypeId) {
        return additionalFunctionService.getAdditionalFunctionByTruckTypeId(truckTypeId);
    }

    /**
     * 获取所有的附件功能及车型基础信息
     */
    @RequestMapping(value = "getAllFunctionAndTypeInfo", method = RequestMethod.GET)
    @ResponseBody
    public TruckRequireInfo getAllFunctionAndTypeInfo(LoginEmployee loginEmployee) {
        return basicTruckTypeService.getBasicTruckRequireInfo(loginEmployee.getTenantId(), null);
    }

    /**
     * 车辆基础信息
     */
    @RequestMapping(value = "truckInfo", method = RequestMethod.GET)
    @ResponseBody
    public TruckVehicle truckInfo(LoginEcoUser driverLoginEcoUser) {
        Driver driver = driverService.findDriverByUserId(driverLoginEcoUser.getUserId());
        if (null == driver) {
            return null;
        }
        return truckService.findTruckVehicleByAmsDriverId(driver.getAmsDriverId(), driverLoginEcoUser);
    }
}
