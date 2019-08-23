package com.juma.tgm.gateway.user.controller;

import com.giants.common.exception.BusinessException;
import com.giants.common.lang.exception.CategoryCodeFormatException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.Department;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.employee.service.DepartmentService;
import com.juma.auth.user.domain.LoginEcoUser;
import com.juma.tgm.user.domain.UserRoute;
import com.juma.tgm.user.domain.UserRouteMaster;
import com.juma.tgm.user.service.UserRouteService;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "user")
public class UserRouteController {

    @Resource
    private UserRouteService userRouteService;

    @Resource
    private DepartmentService departmentService;
    
    @RequestMapping(value = "employee/subcompany", method = RequestMethod.GET)
    @ResponseBody
    public List<Department> getSubCompany(LoginEmployee loginEmployee) throws BusinessException, CategoryCodeFormatException {
        return departmentService.findSubCompanyByEmployee(loginEmployee.getEmployeeId());
    }
    
    @ResponseBody
    @RequestMapping(value = "routes", method = RequestMethod.GET)
    public List<UserRoute> routes(LoginEcoUser cargoOwnerLoginEcoUser) {
        return userRouteService.findRoute(cargoOwnerLoginEcoUser);
    }

    @ResponseBody
    @RequestMapping(value = "route/add", method = RequestMethod.POST)
    public void add(@RequestBody UserRoute userRoute, LoginEcoUser cargoOwnerLoginEcoUser) {
        if (userRoute.getDeliverAddress() != null) {
            userRoute.getDeliverAddress().setUserId(cargoOwnerLoginEcoUser.getUserId());
        }
        this.checkRouteType(userRoute);
        userRouteService.addRoute(userRoute, cargoOwnerLoginEcoUser);
    }

    @ResponseBody
    @RequestMapping(value = "route/update", method = RequestMethod.POST)
    public void update(@RequestBody UserRoute userRoute, LoginEcoUser cargoOwnerLoginEcoUser) {
        this.checkRouteType(userRoute);
        userRouteService.updateRoute(userRoute, cargoOwnerLoginEcoUser);
    }

    /**
     * 检查线路类型是否匹配
     *
     * @param userRoute
     */
    private void checkRouteType(UserRoute userRoute) {
        //如果没有传默认为多配送地
        UserRouteMaster master = userRoute.getDeliverAddress();
        if (master.getBusinessBranch() == null) {
            master.setBusinessBranch(UserRouteMaster.BusinessBranch.TYPE_MULTIPLY.getCode());
        }
        //单配送地只能有一个收货地
        if (NumberUtils.compare(master.getBusinessBranch(), UserRouteMaster.BusinessBranch.TYPE_SINGLE.getCode()) == 0) {
            if (CollectionUtils.size(userRoute.getReceiveAddressList()) > 1)
                throw new BusinessException("receiveAddressListError", "errors.common.prompt", "该类型只能有一个收货地");
        }
    }

    @ResponseBody
    @RequestMapping(value = "route/{masterId}/delete", method = RequestMethod.GET)
    public void delete(@PathVariable Integer masterId, LoginEcoUser cargoOwnerLoginEcoUser) {
        userRouteService.deleteRoute(masterId);
    }

    @ResponseBody
    @RequestMapping(value = "route/detail/{detailId}/delete", method = RequestMethod.GET)
    public void deleteDetail(@PathVariable Integer detailId, LoginEcoUser cargoOwnerLoginEcoUser) {
        userRouteService.deleteRouteDetail(detailId);
    }

    @ResponseBody
    @RequestMapping(value = "route/{masterId}/get", method = RequestMethod.GET)
    public UserRoute get(@PathVariable Integer masterId, LoginEcoUser cargoOwnerLoginEcoUser) {
        return userRouteService.getUserRoute(masterId);
    }

    /**
     * 货主端获取常用路线
     *
     * @param pageCondition
     * @param type
     * @param cargoOwnerLoginEcoUser
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "routes/{type}", method = RequestMethod.POST)
    public Page<UserRoute> routes(@RequestBody PageCondition pageCondition, @PathVariable(value = "type") Integer type, LoginEcoUser cargoOwnerLoginEcoUser) {
        if (type != null) {
            pageCondition.getFilters().put("businessBranch", type);
        }
        pageCondition.getFilters().put("userId", cargoOwnerLoginEcoUser.getUserId());
        pageCondition.getFilters().put("tenantId", cargoOwnerLoginEcoUser.getTenantId());
        return userRouteService.search(pageCondition);
    }

    /**
     * 客户经理获取常用线路
     * @param pageCondition
     * @param truckCustomerId
     * @param type
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "routes/{truckCustomerId}/{type}", method = RequestMethod.POST)
    public Page<UserRoute> routeForManager(@RequestBody PageCondition pageCondition, @PathVariable("truckCustomerId") Integer truckCustomerId, @PathVariable(value = "type") Integer type) {
        return null;
    }

}
