/**
* @Title: VersionController.java
* @Package com.juma.tgm.gateway.version.controller
*<B>Copyright</B> Copyright (c) 2016 www.jumapeisong.com All rights reserved. <br />
* 本软件源代码版权归驹马,未经许可不得任意复制与传播.<br />
* <B>Company</B> 驹马配送
* @date 2016年6月12日 上午11:11:04
* @version V1.0  
 */
package com.juma.tgm.gateway.version.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.juma.tgm.version.domain.Version;
import com.juma.tgm.version.service.VersionService;

/**
 * @Description:
 * @author Administrator
 * @date 2016年6月12日 上午11:11:04
 * @version V1.0
 */
@Controller
@RequestMapping(value = "version")
public class VersionController {

    @Resource
    private VersionService versionService;

    @RequestMapping(value = "check", method = RequestMethod.POST)
    @ResponseBody
    public Version versionCheck(@RequestBody Version version, BindingResult bindingResult) {
        Version example = new Version();
        example.setPackageName(version.getPackageName());
        example.setPlatform(version.getPlatform());
        Version versionDb = versionService.checkNewVersion(example);
        if (versionDb == null)
            return null;
        if (version.getVersionCode() < versionDb.getVersionCode()) {
            return versionDb;
        }
        return null;
    }

}
