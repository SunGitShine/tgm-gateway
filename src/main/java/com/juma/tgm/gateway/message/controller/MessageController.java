/**
 * @Title: MessageController.java
 * @Package com.juma.tgm.gateway.message.controller
 * <B>Copyright</B> Copyright (c) 2016 www.jumapeisong.com All rights reserved. <br />
 * 本软件源代码版权归驹马,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 驹马配送
 * @date 2016年5月23日 上午10:44:51
 * @version V1.0
 */
package com.juma.tgm.gateway.message.controller;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.user.domain.LoginEcoUser;
import com.juma.message.domain.MsgAppRecord;
import com.juma.message.gateway.service.MessageServiceProvider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Administrator
 * @version V1.0
 * @Description:
 * @date 2016年5月23日 上午10:44:51
 */
@Controller
@RequestMapping(value = "message")
public class MessageController {

    @Resource
    private MessageServiceProvider messageServiceProvider;

    @RequestMapping(value = "driver/pushlist", method = RequestMethod.POST)
    @ResponseBody
    public Page<MsgAppRecord> driverPushlist(HttpServletRequest request, @RequestBody PageCondition pageCondition, LoginEcoUser driverLoginEcoUser) {
        pageCondition.getFilters().put("receiveUserId", driverLoginEcoUser.getUserId());
        Page<MsgAppRecord> page = messageServiceProvider.searchDetails(pageCondition, driverLoginEcoUser);
        return reBuildResult(request, page);
    }

    @RequestMapping(value = "truckCustomer/pushlist", method = RequestMethod.POST)
    @ResponseBody
    public Page<MsgAppRecord> customerPushList(HttpServletRequest request, @RequestBody PageCondition pageCondition,
                                               LoginEmployee loginEmployee) {
        pageCondition.getFilters().put("receiveUserId", loginEmployee.getUserId());
        Page<MsgAppRecord> page = messageServiceProvider.searchDetails(pageCondition, loginEmployee);
        return reBuildResult(request, page);
    }

    @RequestMapping(value = "cargoOwner/pushlist", method = RequestMethod.POST)
    @ResponseBody
    public Page<MsgAppRecord> cargoOwnerPushList(HttpServletRequest request, @RequestBody PageCondition pageCondition, LoginEcoUser cargoOwnerLoginEcoUser) {
        pageCondition.getFilters().put("receiveUserId", cargoOwnerLoginEcoUser.getUserId());
        Page<MsgAppRecord> page = messageServiceProvider.searchDetails(pageCondition, cargoOwnerLoginEcoUser);
        return reBuildResult(request, page);
    }

    private Page<MsgAppRecord> reBuildResult(HttpServletRequest request, Page<MsgAppRecord> page) {
        for (MsgAppRecord record : page.getResults()) {
            String clickLink = record.getClickLink();
            if (fetchDevice(request) == Device.ANDROID && clickLink != null) {
                clickLink = clickLink.replaceAll("https", "http");
            }
            record.setClickLink(clickLink);
        }
        return page;
    }


    private Device fetchDevice(HttpServletRequest request) {
        String agent = request.getHeader("user-agent");
        if (agent.contains("Android")) {
            return Device.ANDROID;
        }
        if (agent.contains("iPhone") || agent.contains("iPod") || agent.contains("iPad")) {
            return Device.IOS;
        }
        return Device.UNKNOW;
    }

    public enum Device {

        UNKNOW(0, "未知"), IOS(1, "IOS"), ANDROID(2, "ANDROID");

        private int code;

        private String descr;

        private Device(int code, String descr) {
            this.code = code;
            this.descr = descr;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDescr() {
            return descr;
        }

        public void setDescr(String descr) {
            this.descr = descr;
        }

    }
}
