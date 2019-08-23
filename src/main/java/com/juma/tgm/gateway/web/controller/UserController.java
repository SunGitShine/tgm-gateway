package com.juma.tgm.gateway.web.controller;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.juma.tgm.gateway.web.controller.vo.UserLocation;
import com.juma.tgm.region.service.RegionTgmService;

@Controller
public class UserController {
    
    
    @Resource
    private RegionTgmService regionTgmService;
    
    @RequestMapping(value = "user/region")
    @ResponseBody
    public String userRegion(@RequestBody UserLocation userLocation)  {
        if(StringUtils.isBlank(userLocation.getCoordinate())) {
            throw new IllegalArgumentException("coordinate 参数为空");
        }
        return regionTgmService.findRegionCodeByCoordinate(userLocation.getCoordinate());
    }

}
