package com.juma.tgm.gateway.select.controller;

import java.util.List;

import javax.annotation.Resource;

import com.juma.tgm.common.query.QueryCond;
import com.juma.tgm.common.vo.Page;
import com.juma.tgm.select.vo.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.crm.domain.CustomerInfo;
import com.juma.tgm.customer.domain.vo.CustomerInfoFilter;
import com.juma.tgm.project.domain.v2.Project;
import com.juma.tgm.project.service.ProjectService;
import com.juma.tgm.project.vo.ProjectFilter;
import com.juma.tgm.select.service.SelectService;
import com.juma.vms.truck.vo.TruckFilters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value = "下拉选择管理")
@RestController
@RequestMapping(value = "select")
public class SelectController {

    @Resource
    private SelectService selectService;

    @Resource
    private ProjectService projectService;

    @ApiOperation(value = "运力列表",notes = "下拉管理")
    @RequestMapping(value = "capacity",method = RequestMethod.POST)
    public Page<Capacity> pageOfCapacity (@RequestBody QueryCond<CapacityFilter> filter, LoginEmployee loginEmployee) {
        filter.getFilters().setPageNo(filter.getPageNo());
        filter.getFilters().setPageSize(filter.getPageSize());
        return selectService.pageOfCapacity(filter.getFilters(),filter.getPageSize(),loginEmployee);
    }

    @ApiOperation(value = "司机列表",notes = "下拉管理")
    @RequestMapping(value = "driver",method = RequestMethod.POST)
    public List<DriverSelect> pageOfDriver (@RequestBody DriverFilter filter, LoginEmployee loginEmployee) {
        return selectService.pageOfDriver(filter,filter.getPageSize(),loginEmployee);
    }

    @ApiOperation(value = "车辆列表",notes = "下拉管理")
    @RequestMapping(value = "truck",method = RequestMethod.POST)
    public List<TruckSelect> pageOfTruck (@RequestBody TruckFilter filter, LoginEmployee loginEmployee) {
        return selectService.pageOfTruck(filter,filter.getPageSize(),loginEmployee);
    }

    @ApiOperation(value = "承运商列表",notes = "下拉管理")
    @RequestMapping(value = "vendor",method = RequestMethod.POST)
    public List<VendorSelect> pageOfVendor (@RequestBody VendorFilter filter, LoginEmployee loginEmployee) {
        return selectService.pageOfVendor(filter,filter.getPageSize(),loginEmployee);
    }

    @ApiOperation(value = "根据项目名称模糊查询登录人有权限的项目")
    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "项目名称，可不传", dataType = "String")})
    @ResponseBody
    @RequestMapping(value = "project", method = RequestMethod.POST)
    public List<Project> listProjectByLoginUser(@RequestBody ProjectFilter filter,
        @ApiParam(hidden = true) LoginEmployee loginEmployee) {
        return projectService.listProjectBy(filter,filter.getBackPageSize(), loginEmployee);
    }

    @ApiOperation(value = "根据客户名称模糊获取当前登录人所有的客户信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "customerType", value = "1、司机，2、货主", dataType = "Integer"),
        @ApiImplicitParam(name = "customerName", value = "客户名称", dataType = "String"),
        @ApiImplicitParam(name = "statusNotEquals", value = "传2剔除已淘汰客户", dataType = "String"),
    })
    @ResponseBody
    @RequestMapping(value = "customerInfo", method = RequestMethod.POST)
    public List<CustomerInfo> allAreaCustomerInfo(@RequestBody CustomerInfoFilter filter, @ApiParam(hidden = true) LoginEmployee loginEmployee) {
        return selectService.listCustomerInfo(filter,filter.getPageSize(), loginEmployee);
    }
}
