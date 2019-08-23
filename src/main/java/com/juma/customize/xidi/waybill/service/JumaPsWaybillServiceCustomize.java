package com.juma.customize.xidi.waybill.service;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import com.giants.common.exception.BusinessException;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.user.domain.LoginUser;
import com.juma.customize.annotation.CustomizeLayer;
import com.juma.customize.annotation.Customized;
import com.juma.tgm.common.Constants;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.service.WaybillService;
import com.juma.tgm.waybill.service.customize.jumaPs.JumaPsWaybillService;

/**
 * @ClassName: JumaPsWaybillServiceCustomize
 * @Description:
 * @author: liang
 * @date: 2018-03-28 14:13
 * @Copyright: 2018 www.jumapeisong.com Inc. All rights reserved.
 */
@Customized(tenantKey = Constants.TENANT_KEY_JUMA_LOGISTICS, layer = CustomizeLayer.service)
@Component
public class JumaPsWaybillServiceCustomize {

    @Resource
    private WaybillService waybillService;

    @Resource
    private JumaPsWaybillService jumaPsWaybillService;

    public void changeToAssignedBatch(List<Waybill> waybills, LoginUser loginUser) throws BusinessException {
        //专车只能指派一个车
        if (CollectionUtils.size(waybills) > 1)
            throw new BusinessException("waybillSizeError", "errors.canMoreThan", new String[]{"运单数量", "1"});

        waybillService.changeToAssignedBatch(waybills, loginUser);
    }

    public Waybill customerManagerModifyFreight(Waybill waybill, LoginEmployee loginEmployee) throws BusinessException {
        return jumaPsWaybillService.customerManagerModifyFreight(waybill, loginEmployee);
    }
}
