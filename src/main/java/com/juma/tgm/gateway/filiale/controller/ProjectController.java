package com.juma.tgm.gateway.filiale.controller;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.base.domain.BaseEnumDomian;
import com.juma.tgm.common.FreightEnum;
import com.juma.tgm.configure.domain.FreightFactor;
import com.juma.tgm.configure.service.FreightFactorService;
import com.juma.tgm.customer.service.CustomerManagerService;
import com.juma.tgm.gateway.common.BaseController;
import com.juma.tgm.project.domain.Project;
import com.juma.tgm.project.service.ProjectService;
import com.juma.tgm.project.vo.ProjectVo;
import com.juma.tgm.truck.domain.AdditionalFunction;
import com.juma.tgm.truck.domain.AdditionalFunctionBo;
import com.juma.tgm.truck.domain.TruckType;
import com.juma.tgm.truck.domain.bo.DriverTruckInfoBo;
import com.juma.tgm.truck.service.AdditionalFunctionService;
import com.juma.tgm.truck.service.TruckTypeService;
import com.juma.tgm.waybill.domain.TaxRate;
import com.juma.tgm.waybill.service.TaxRateService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: ProjectController
 * @Description:
 * @author: liang
 * @date: 2017-09-27 17:30
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
@RequestMapping(value = "filiale")
@Controller
public class ProjectController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);

    @Resource
    private ProjectService projectService;

    @Resource
    private TruckTypeService truckTypeService;

    @Resource
    private CustomerManagerService customerManagerService;

    @Resource
    private TaxRateService taxRateService;

    @Resource
    private AdditionalFunctionService additionalFunctionService;

    @Resource
    private FreightFactorService freightFactorService;

    //搜索列表
    @RequestMapping(value = "project/search", method = RequestMethod.POST)
    @ResponseBody
    public Page<ProjectVo> search(@RequestBody PageCondition pageCondition, LoginEmployee loginEmployee) {

        Page<ProjectVo> pageData = new Page<>(pageCondition.getPageNo(), pageCondition.getPageSize(), 0);
        List<Integer> customerManagerIdList = new ArrayList<>();
        customerManagerIdList.add(loginEmployee.getEmployeeId());
        pageCondition.getFilters().put("customerManagerIdList", customerManagerIdList);
        Page<Project> rawData = projectService.search(loginEmployee, pageCondition);
        List<ProjectVo> projectVoList = this.buildBasicInfo(rawData.getResults(), loginEmployee);
        pageData.setResults(projectVoList);
        pageData.setTotal(rawData.getTotal());
        return pageData;
    }

    /**
     * 搜索项目基础信息
     *
     * @param pageCondition
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "project/basicList", method = RequestMethod.POST)
    @ResponseBody
    public Page<Project> searchBase(@RequestBody PageCondition pageCondition, LoginEmployee loginEmployee) {
        //获取当前经济人下的客户
//        List<CustomerInfo> customerList = customerInfoService.findCustomerInfoByCustomerManagerId(loginEmployee.getEmployeeId());
//
//        Page<Project> pageData = new Page<>(pageCondition.getPageNo(), pageCondition.getPageSize(), 0);
//        if (CollectionUtils.isEmpty(customerList)) return pageData;

//        List<Integer> customerIds = new ArrayList<>();
//        for (CustomerInfo info : customerList) {
//            customerIds.add(info.getCustomerId());
//        }
//        pageCondition.put("customerIds", customerIds);
        List<Integer> customerManagerIdList = new ArrayList<>();
        customerManagerIdList.add(loginEmployee.getEmployeeId());
        pageCondition.getFilters().put("customerManagerIdList", customerManagerIdList);
        Page<Project> rawData = projectService.search(loginEmployee, pageCondition);

        return rawData;
    }

    /**
     * 项目管理列表信息
     *
     * @param projects
     * @return
     */
    private List<ProjectVo> buildBasicInfo(Collection<Project> projects, LoginEmployee loginEmployee) {
        List<ProjectVo> projectVoList = new ArrayList<>();
        if (CollectionUtils.isEmpty(projects)) return projectVoList;
        List<FreightFactor> freightFactors = freightFactorService.findByFreightWay(FreightEnum.PROJECT.getCode(), loginEmployee);
        ProjectVo vo = null;
        for (Project pjt : projects) {
            vo = new ProjectVo();
            projectVoList.add(vo);
            BeanUtils.copyProperties(pjt, vo);
            //用车人
//            this.buildTruckCustomerInfo(vo);
            //计价方式
//            this.buildFreightRule(vo, loginEmployee);
//            vo.setAllFactors(freightFactors);
        }

        return projectVoList;
    }

    //添加项目

    /**
     * 添加项目
     *
     * @param project
     * @param loginEmployee
     */
    @RequestMapping(value = "project/add", method = RequestMethod.POST)
    @ResponseBody
    public void addProject(@RequestBody Project project, LoginEmployee loginEmployee) {
        project.setManagerId(loginEmployee.getEmployeeId());
        projectService.add(project, loginEmployee);
    }

    //获取项目详情

    /**
     * 获取项目详情
     *
     * @param projectId
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "project/{projectId}/detail", method = RequestMethod.GET)
    @ResponseBody
    public ProjectVo getDetail(@PathVariable(value = "projectId") Integer projectId, LoginEmployee loginEmployee) {
        Project project = projectService.getProject(projectId);

        ProjectVo vo = new ProjectVo();
        BeanUtils.copyProperties(project, vo);

//        //用车人
//        this.buildTruckCustomerInfo(vo);
//        //计费规则
//        this.buildFreightRule(vo, loginEmployee);
//        //客户信息
//        this.buildCustomerInfo(vo);
//        //车辆信息
//        this.buildProjectTruck(vo, loginEmployee);
//        //线路信息
//        this.buildProjectAddressInfo(vo);

        return vo;
    }

    //修改项目信息

    /**
     * 修改项目信息
     *
     * @param project
     * @param loginEmployee
     */
    @RequestMapping(value = "project/update", method = RequestMethod.POST)
    @ResponseBody
    public void update(@RequestBody Project project, LoginEmployee loginEmployee) {
        projectService.update(project, loginEmployee);
    }

    /**
     * 项目管理-获取计价规则信息
     *
     * @return
     */
    @RequestMapping(value = "project/freightRule", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getFreightRule(LoginEmployee loginEmployee) {
        Map<String, Object> rst = new HashMap<>();
        //计费方式
        List<FreightFactor> freightFactors = freightFactorService.findByFreightWay(FreightEnum.PROJECT.getCode(), loginEmployee);
        rst.put("FreightFactor", freightFactors);
        //车型
        List<TruckType> truckTypes = truckTypeService.listAllTruckTypeByOrderNoAsc(loginEmployee.getTenantId(), false);
//        rst.put("TruckType", truckTypes);
        //箱型分组车型
        rst.put("groupTruckType", this.groupTruckType(truckTypes));
        //税率
        List<TaxRate> taxRates = taxRateService.loadByTenant(loginEmployee);
        rst.put("TaxRate", taxRates);
        // 用车要求
        List<AdditionalFunctionBo> functionList = additionalFunctionService.getFunctionList();
//        for (int i = functionList.size() - 1; i >= 0; i--) {
//            AdditionalFunctionBo functionBo = functionList.get(i);
//            if (AdditionalFunction.FunctionKeys.DELIVERY_RECEIPT.toString().equals(functionBo.getFunctionKey())
//                    || AdditionalFunction.FunctionKeys.NEXT_DAY_DELIVERY.toString().equals(functionBo.getFunctionKey())) {
//                functionList.remove(functionBo);
//            }
//        }
        rst.put("funList", functionList);
        return rst;
    }

    //箱型分组
    public List<Map<String, List<TruckType>>> groupTruckType(List<TruckType> allTruckType) {
        List<Map<String, List<TruckType>>> finalData = new ArrayList<>();
        if (CollectionUtils.isEmpty(allTruckType)) return finalData;
        Map<String, List<TruckType>> allTruckTypeMap = new HashMap<>();

        //获取所有箱型列表
        List<BaseEnumDomian> boxTypes = truckTypeService.listVehicleBoxType();
        if (CollectionUtils.isEmpty(boxTypes)) return finalData;

        Map<Integer, String> allBoxTypesMap = new HashMap<>();

        for (BaseEnumDomian base : boxTypes) {
            allBoxTypesMap.put(base.getCode(), base.getDesc());
        }

        for (TruckType type : allTruckType) {
            String boxName = allBoxTypesMap.get(type.getVehicleBoxType());
            //有则在list中增加
            if (allTruckTypeMap.containsKey(boxName)) {
                List<TruckType> truckTypes = allTruckTypeMap.get(boxName);
                truckTypes.add(type);
            } else {
                //map中没有类型则新增
                List<TruckType> tmpList = new ArrayList<>();
                allTruckTypeMap.put(boxName, tmpList);
                tmpList.add(type);
            }
        }

        Map<String, List<TruckType>> groupMap = null;
        for (String key : allTruckTypeMap.keySet()) {
            groupMap = new HashMap<>();
            groupMap.put(key, allTruckTypeMap.get(key));
            finalData.add(groupMap);
        }

        return finalData;
    }

    /**
     * 车队
     *
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "project/truckFleet/{projectId}", method = RequestMethod.GET)
    @ResponseBody
    public Page<DriverTruckInfoBo> getTruckFleet(@PathVariable(value = "projectId") Integer projectId, Integer pageNo,
            Integer pageSize, LoginEmployee loginEmployee) {
        return customerManagerService.findTruckFleetForFilialeProject(projectId, pageNo, pageSize, loginEmployee);
    }

    //重新组装用车要求

    /**
     * 项目下单-重新组装用车要求
     *
     * @return
     */
    @RequestMapping(value = "project/require", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getRequireInfo(LoginEmployee loginEmployee) {
        Map<String, Object> rst = new HashMap<>();
        //车辆要求
        //车型
        List<TruckType> truckTypes = truckTypeService.listAllTruckTypeByOrderNoAsc(loginEmployee.getTenantId(), false);
        rst.put("TruckType", truckTypes);
        //税率
        List<TaxRate> taxRates = taxRateService.loadByTenant(loginEmployee);
        rst.put("TaxRate", taxRates);
        //配送要求
        List<AdditionalFunction> allFunctions = additionalFunctionService.getAllAdditionalFunction();
        if (CollectionUtils.isEmpty(allFunctions)) return rst;

        String[] targetFuns = new String[]{AdditionalFunction.FunctionKeys.NEED_BACK_STORAGE.name(), AdditionalFunction.FunctionKeys.DRIVER_CARRY.name(), AdditionalFunction.FunctionKeys.LABORER_CARRY.name()};
        Arrays.sort(targetFuns);
        List<AdditionalFunction> destFunctions = new ArrayList<>();
        //返仓
        //司机搬运
        //小工搬运
        for (AdditionalFunction bo : allFunctions) {
            if (Arrays.binarySearch(targetFuns, bo.getFunctionKey()) >= 0) {
                destFunctions.add(bo);
            }
        }
        rst.put("deliveryRequire", destFunctions);
        return rst;
    }

}
