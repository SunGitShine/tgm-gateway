package com.juma.tgm.gateway.cms.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.juma.tgm.common.Constants;
import com.juma.tgm.configure.service.ServiceConfService;

@Controller
@RequestMapping("conf")
public class ServiceConfController {

    @Resource
    private ServiceConfService serviceConfService;

    /**
     * 客服电话
     */
    @ResponseBody
    @RequestMapping(value = "serviceTel", method = RequestMethod.GET)
    public String serviceTel() {
        return serviceConfService.findCustomerServiceTel("100", Constants.SYS_LOGIN_USER);
    }

}
