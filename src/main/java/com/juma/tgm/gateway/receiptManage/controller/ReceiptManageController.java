package com.juma.tgm.gateway.receiptManage.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.juma.auth.user.domain.LoginEcoUser;
import com.juma.tgm.receiptManage.domain.ReceiptManage;
import com.juma.tgm.receiptManage.service.ReceiptManageService;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.service.WaybillCommonService;

/**
 * Created by shawn_lin on 2017/7/10.
 */
@Controller
@RequestMapping("receipt/manage")
public class ReceiptManageController {

    @Resource
    private ReceiptManageService receiptManageService;
    @Resource
    private WaybillCommonService waybillCommonService;

    /**
     * 图片上传
     */
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    @ResponseBody
    public void uploadReceiptImage(@RequestBody ReceiptManage receiptManage, LoginEcoUser driverLoginEcoUser) {
        // 判断运单是不是承运运单，若是承运运单，则使用原单ID添加
        Waybill waybill = waybillCommonService.findWaybillByTransformBillId(receiptManage.getWaybillId());
        if (null != waybill) {
            receiptManage.setWaybillId(waybill.getWaybillId());
        }

        receiptManageService.insert(receiptManage, driverLoginEcoUser);
    }

    /**
     * 回单回显
     */
    @RequestMapping(value = "image/{waybillId}/show", method = RequestMethod.GET)
    @ResponseBody
    public List<ReceiptManage> showReceipt(@PathVariable Integer waybillId) {
        return receiptManageService.listByWaybillId(waybillId);
    }
}
