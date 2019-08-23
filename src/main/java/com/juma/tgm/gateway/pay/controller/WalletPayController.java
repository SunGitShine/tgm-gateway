/**
* @Title: PayController.java
* @Package com.juma.tgm.gateway.pay.controller
*<B>Copyright</B> Copyright (c) 2016 www.jumapeisong.com All rights reserved. <br />
* 本软件源代码版权归驹马,未经许可不得任意复制与传播.<br />
* <B>Company</B> 驹马配送
* @date 2016年8月1日 下午3:42:41
* @version V1.0  
 */
package com.juma.tgm.gateway.pay.controller;

import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.user.domain.LoginEcoUser;
import com.juma.tgm.pay.domain.TransactionResponse;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.bo.PrePaySign;
import com.juma.tgm.waybill.service.WaybillService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @Description: 钱包支付
 * @author Administrator
 * @date 2016年8月1日 下午3:42:41
 * @version V1.0
 */

@Controller
@RequestMapping(value = "pay")
public class WalletPayController {

    @Resource
    private WaybillService waybillService;

    @RequestMapping(value = "callback")
    @ResponseBody
    public void callback(@RequestBody TransactionResponse response) {
        waybillService.payCallback(response);
    }

    /**
     * 货主端使用
     */
    @ResponseBody
    @RequestMapping(value = "customer/prePaySign", method = RequestMethod.POST)
    public PrePaySign doCustomerPrePaySign(@RequestBody Waybill waybill, LoginEcoUser cargoOwnerLoginEcoUser) {
        return waybillService.doPrePaySign(waybill);
    }

    /**
     * 客户经理端使用
     */
    @ResponseBody
    @RequestMapping(value = "manager/prePaySign", method = RequestMethod.POST)
    public PrePaySign doManagerPrePaySign(@RequestBody Waybill waybill, LoginEmployee loginEmployee) {
        return waybillService.doPrePaySign(waybill);
    }
}
