package com.juma.tgm.gateway.common;

import com.juma.auth.authority.resolver.EcoUserSessionHandlerMethodArgumentResolver;
import com.juma.auth.user.domain.LoginEcoUser;
import com.juma.tgm.driver.domain.Driver;
import com.juma.tgm.driver.domain.DriverLoginUser;
import com.juma.tgm.driver.service.DriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.annotation.Resource;

/**
 * @ClassName: DriverResolver
 * @Description:
 * @author: liang
 * @date: 2017-05-25 17:53
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
public class DriverResolver extends EcoUserSessionHandlerMethodArgumentResolver {

    private static final Logger log = LoggerFactory.getLogger(DriverResolver.class);

    @Resource
    private DriverService driverService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.getParameterType().equals(DriverLoginUser.class)
                && parameter.getParameterName().equals("driverLoginUser")) {
            return true;
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        LoginEcoUser loginEcoUser = (LoginEcoUser) super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        return buildDriverLoginUser(loginEcoUser);
    }


    public DriverLoginUser buildDriverLoginUser(LoginEcoUser loginEcoUser) {
        if (loginEcoUser == null) {
            return new DriverLoginUser();
        }

        try {
            return this.createRealTimeDriverLoginUser(loginEcoUser);
        } catch (Exception e) {
            log.error("注入@ModelAttribute(driverLoginUser)错误", e);
        }

        return new DriverLoginUser();
    }


    private DriverLoginUser createRealTimeDriverLoginUser(LoginEcoUser loginEcoUser) {
        Driver driver = driverService.findDriverByUserId(loginEcoUser.getUserId());

        if (driver == null) {
            return new DriverLoginUser();
        }

        DriverLoginUser driverUser = new DriverLoginUser();
        driverUser.setDriverId(driver.getDriverId());
        driverUser.setLoginEcoUser(loginEcoUser);

        return driverUser;
    }
}
