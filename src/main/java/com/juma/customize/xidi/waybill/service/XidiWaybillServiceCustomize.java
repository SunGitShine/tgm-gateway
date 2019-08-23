package com.juma.customize.xidi.waybill.service;

import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.user.domain.LoginEcoUser;
import com.juma.auth.user.domain.LoginUser;
import com.juma.customize.annotation.CustomizeLayer;
import com.juma.customize.annotation.Customized;
import com.juma.tgm.common.Constants;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillBo;
import com.juma.tgm.waybill.domain.WaybillDetailInfo;
import com.juma.tgm.waybill.service.customize.xidi.XidiWaybillService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName: XidiWaybillServiceCustomize
 * @Description:
 * @author: liang
 * @date: 2018-03-15 11:01
 * @Copyright: 2018 www.jumapeisong.com Inc. All rights reserved.
 */
@Customized(tenantKey = Constants.TENANT_KEY_XIDI_LOGISTICS, layer = CustomizeLayer.service)
@Component
public class XidiWaybillServiceCustomize {
    // public class XidiWaybillServiceCustomize implements XidiWaybillService {
    @Resource
    private XidiWaybillService xidiWaybillService;

    // @Resource
    // private ScatteredWaybillService scatteredWaybillService;

    // @Override
    public void changeCar(int waybillId, int driverId, int truckId, int flightId, int receiveWay, String remark,
            LoginUser loginUser) throws BusinessException {
        xidiWaybillService.changeCar(waybillId, driverId, truckId, flightId, receiveWay, remark, loginUser);
    }

    // @Override
    public void cancelWaybill(Integer waybillId, Waybill.CancelChannel cancelChannel, String waybillCancelRemark,
            LoginEmployee loginEmployee) throws BusinessException {
        xidiWaybillService.cancelWaybill(waybillId, cancelChannel, waybillCancelRemark, loginEmployee);
    }

    // @Override
    public void updateArriveDepotTime(Waybill waybill, LoginEcoUser loginEcoUser) throws BusinessException {
        xidiWaybillService.updateArriveDepotTime(waybill, loginEcoUser);
    }

    // @Override
    public void updateLeaveDepotTime(Waybill waybill, LoginEcoUser loginEcoUser) throws BusinessException {
        xidiWaybillService.updateLeaveDepotTime(waybill, loginEcoUser);
    }

    // @Override
    public void changeToAssigned(int waybillId, int driverId, int truckId, int flightId, int receiveWay, String remark,
            LoginUser loginUser) throws BusinessException {
        xidiWaybillService.changeToAssigned(waybillId, driverId, truckId, flightId, receiveWay, remark, loginUser);
    }

    // @Override
    public WaybillDetailInfo getWaybillInfo(Integer waybillId, LoginUser loginUser) throws BusinessException {
        return xidiWaybillService.getWaybillInfo(waybillId, loginUser);
    }

    // @Override
    public Page<WaybillBo> getPageForAcceptableWaybillList(PageCondition pageCondition, LoginUser loginUser)
            throws BusinessException {
        return xidiWaybillService.getPageForAcceptableWaybillList(pageCondition, loginUser);
    }

    // @Override
    public boolean allowChangeCar(Waybill waybill) throws BusinessException {
        return xidiWaybillService.allowChangeCar(waybill);
    }

    // @Override
    public Page<Waybill> search(PageCondition pageCondition, LoginUser loginUser) throws BusinessException {
        return xidiWaybillService.search(pageCondition, loginUser);
    }

    // @Override
    public WaybillDetailInfo findWaybillDetailById(Integer waybillId, LoginUser loginUser) throws BusinessException {
        return xidiWaybillService.findWaybillDetailById(waybillId, loginUser);
    }

    public void changeToDeliveried(Waybill waybill, LoginUser loginUser) throws BusinessException {
        // scatteredWaybillService.changeToDeliveried(waybill, loginUser);
        xidiWaybillService.changeToDeliveried(waybill, loginUser);
    }
}
