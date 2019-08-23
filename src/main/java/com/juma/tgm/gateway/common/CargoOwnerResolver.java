package com.juma.tgm.gateway.common;

import com.juma.auth.authority.resolver.EcoUserSessionHandlerMethodArgumentResolver;
import com.juma.auth.user.domain.LoginEcoUser;
import com.juma.tgm.customer.domain.CargoOwnerLoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @ClassName: CargoOwnerResolver
 * @Description:
 * @author: liang
 * @date: 2017-05-25 17:58
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
public class CargoOwnerResolver extends EcoUserSessionHandlerMethodArgumentResolver {

    private static final Logger log = LoggerFactory.getLogger(CargoOwnerResolver.class);

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.getParameterType().equals(CargoOwnerLoginUser.class)
                && parameter.getParameterName().equals("cargoOwnerLoginUser")) {
            return true;
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        LoginEcoUser loginEcoUser = (LoginEcoUser)super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        return this.buildCargoOwnerLoginUser(loginEcoUser);
    }


    public CargoOwnerLoginUser buildCargoOwnerLoginUser(LoginEcoUser loginEcoUser) {
        if (loginEcoUser == null) {
            return new CargoOwnerLoginUser();
        }

        try {
            return this.createRealTimeCargoOwnerLoginUser(loginEcoUser);
        } catch (Exception e) {
            log.error("注入@ModelAttribute(CargoOwnerLoginUser)错误", e);
        }
        return new CargoOwnerLoginUser();
    }

    private CargoOwnerLoginUser createRealTimeCargoOwnerLoginUser(LoginEcoUser loginEcoUser) {
        return null;
    }
}
