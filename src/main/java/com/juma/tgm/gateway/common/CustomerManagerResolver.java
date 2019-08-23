package com.juma.tgm.gateway.common;

import com.juma.auth.authority.resolver.EmployeeSessionHandlerMethodArgumentResolver;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.customer.domain.CustomerLoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @ClassName: LoginEmployeeResolver
 * @Description:
 * @author: liang
 * @date: 2017-05-25 16:55
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
public class CustomerManagerResolver extends EmployeeSessionHandlerMethodArgumentResolver {

    private static final Logger log = LoggerFactory.getLogger(CustomerManagerResolver.class);

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.getParameterType().equals(CustomerLoginUser.class)
                && parameter.getParameterName().equals("customerLoginUser")) {
            return true;
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        LoginEmployee loginEmployee = (LoginEmployee) super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        return this.buildCustomerLoginUser(loginEmployee);
    }


    public CustomerLoginUser buildCustomerLoginUser(LoginEmployee loginEmployee) {
        if (loginEmployee == null) {
            return new CustomerLoginUser();
        }

        try {
            return this.createRealTimeCustomerLoginUser(loginEmployee);
        } catch (Exception e) {
            log.error("注入@ModelAttribute(customerLoginUser)错误", e);
        }

        return new CustomerLoginUser();
    }


    private CustomerLoginUser createRealTimeCustomerLoginUser(LoginEmployee loginEmployee) {

        if (loginEmployee == null) {
            return null;
        }

        CustomerLoginUser custUser = new CustomerLoginUser();
        custUser.setLoginEmployee(loginEmployee);

        return custUser;
    }
}
