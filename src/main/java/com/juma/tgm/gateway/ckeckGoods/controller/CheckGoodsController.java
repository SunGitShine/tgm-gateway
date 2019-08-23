package com.juma.tgm.gateway.ckeckGoods.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.exception.BusinessException;
import com.juma.auth.user.domain.LoginEcoUser;
import com.juma.tgm.gateway.ckeckGoods.vo.CheckGoodsVO;
import com.juma.tgm.imageUploadManage.domain.ImageUploadManage;
import com.juma.tgm.imageUploadManage.service.ImageUploadManageService;
import com.juma.tgm.waybill.domain.WaybillParam;
import com.juma.tgm.waybill.service.WaybillParamService;

/**
 * 
 * @ClassName CheckGoodsController.java 货物检查
 * @author Libin.Wei
 * @Date 2018年7月26日 上午11:14:46
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */
@Controller
@RequestMapping("check/goods")
public class CheckGoodsController {

    @Resource
    private ImageUploadManageService imageUploadManageService;
    @Resource
    private WaybillParamService waybillParamService;

    /**
     * 确认已验收货物
     */
    @RequestMapping(value = "{waybillId}/confirm", method = RequestMethod.GET)
    @ResponseBody
    public void confirmHasCheckGoods(@PathVariable Integer waybillId, LoginEcoUser driverLoginEcoUser) {
        waybillParamService.confirmHasCheckGoods(waybillId, driverLoginEcoUser);

        // 检查是否变更成功
        WaybillParam waybillParam = waybillParamService.findByWaybillId(waybillId);
        if (null == waybillParam || null == waybillParam.getIsCheckGoods()
                || !waybillParam.getIsCheckGoods().equals(1)) {
            throw new BusinessException("confirmHasCheckGoodsFaild", "waybillParam.error.confirmHasCheckGoodsFaild");
        }
    }

    /**
     * 图片上传
     */
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    @ResponseBody
    public void uploadReceiptImage(@RequestBody CheckGoodsVO checkGoodsVO, LoginEcoUser driverLoginEcoUser) {
        imageUploadManageService.batchInsert(checkGoodsVO.getImgUrls(), checkGoodsVO.getWaybillId(),
                ImageUploadManage.ImageUploadManageSign.GOODS_CHECK, null, driverLoginEcoUser);

        // 同步转运单信息
        WaybillParam transformBillParam = waybillParamService.findByTransformBillLinkId(checkGoodsVO.getWaybillId());
        if (null == transformBillParam) {
            return;
        }

        imageUploadManageService.batchInsert(checkGoodsVO.getImgUrls(), transformBillParam.getWaybillId(),
                ImageUploadManage.ImageUploadManageSign.GOODS_CHECK, null, driverLoginEcoUser);
    }

    /**
     * 图片回显
     */
    @RequestMapping(value = "image/{waybillId}/show", method = RequestMethod.GET)
    @ResponseBody
    public List<String> showReceipt(@PathVariable Integer waybillId) {
        List<String> imgUrls = new ArrayList<String>();
        List<ImageUploadManage> list = imageUploadManageService.listByRelationIdAndSign(waybillId,
                ImageUploadManage.ImageUploadManageSign.GOODS_CHECK.getCode());
        if (list.isEmpty()) {
            return imgUrls;
        }

        for (ImageUploadManage i : list) {
            imgUrls.add(i.getImageUploadUrl());
        }

        return imgUrls;
    }
}
