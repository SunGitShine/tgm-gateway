package com.juma.tgm.gateway.filiale.controller;

import com.giants.common.exception.BusinessException;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.filiale.service.FilialeBillService;
import com.juma.tgm.gateway.common.BaseController;
import com.juma.tgm.project.vo.ProjectBillVo;
import com.juma.tgm.waybill.domain.TaxRate;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillDetailInfo;
import com.juma.tgm.waybill.domain.WaybillOperateTrack;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateApplication;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateType;
import com.juma.tgm.waybill.service.TaxRateService;
import com.juma.tgm.waybill.service.WaybillOperateTrackService;
import com.juma.tgm.waybill.service.WaybillService;
import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: BillController
 * @Description:
 * @author: liang
 * @date: 2017-10-09 10:22
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
@RequestMapping(value = "filiale")
@Controller
public class BillController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(BillController.class);


    @Resource
    private FilialeBillService filialeBillService;

    @Resource
    private WaybillService waybillService;

    @Resource
    private WaybillOperateTrackService waybillOperateTrackService;
    
    @Resource
    private TaxRateService taxRateService;

    /**
     * 项目下单
     *
     * @param projectBillVo
     * @param loginEmployee
     */
    @RequestMapping(value = "bill/create", method = RequestMethod.POST)
    @ResponseBody
    public List<Integer> createBill(@RequestBody ProjectBillVo projectBillVo, LoginEmployee loginEmployee) {
        if (projectBillVo == null) throw new BusinessException("waybillNull", "errors.paramCanNotNullWithName", "参数");

        Waybill waybill = projectBillVo.getWaybill();
        if (waybill == null) throw new BusinessException("waybillNull", "errors.paramCanNotNullWithName", "运单信息");
        waybill.setCustomerManagerId(loginEmployee.getEmployeeId());
        
        waybill.setWaybillSource(Waybill.WaybillSource.JUMA_CLIENT.getCode());
        return filialeBillService.createProjectBill(projectBillVo, loginEmployee);
    }


    /**
     * 获取项目运单详情
     *
     * @param waybillId
     * @return
     */
    @RequestMapping(value = "bill/{waybillId}/detail", method = RequestMethod.GET)
    @ResponseBody
    public WaybillDetailInfo getBillDetail(@PathVariable(value = "waybillId") Integer waybillId, LoginEmployee loginEmployee) {
        WaybillDetailInfo waybillDetailInfo = filialeBillService.getProjectBillDetail(waybillId, loginEmployee);

        return waybillDetailInfo;
    }

    /**
     * 修改费用
     *
     * @param waybill
     * @param loginEmployee
     */
    @RequestMapping(value = "bill/modifyPreTaxFreight", method = RequestMethod.POST)
    @ResponseBody
    public void modifyPreTaxFreight(@RequestBody Waybill waybill, LoginEmployee loginEmployee) {
        Waybill origin = waybillService.getWaybill(waybill.getWaybillId());
        if (origin == null) return;

        filialeBillService.modifyPreTaxFreight(waybill, loginEmployee);

        // 操作轨迹
        String remark = "原价" + origin.getEstimateFreight() + "，更换成" + waybill.getEstimateFreight();
        waybillOperateTrackService.insert(origin.getWaybillId(), OperateType.UPDATE_FREIGHT,
                OperateApplication.CUSTOMER_SYS, buildTrackNotRequieParam(origin, remark),
                loginEmployee);

    }

    /**
     * 项目运单价格确认
     *
     * @param waybillId
     * @param loginEmployee
     */
    @RequestMapping(value = "bill/{waybillId}/complete", method = RequestMethod.POST)
    @ResponseBody
    public void changeBillToComplete(@PathVariable(value = "waybillId") Integer waybillId, LoginEmployee loginEmployee) {
        Waybill origin = waybillService.getWaybill(waybillId);
        filialeBillService.changeToComplete(waybillId, loginEmployee);
        // 操作轨迹
        String remark = "运单状态" + origin.getStatus() + "，更换成" + Waybill.Status.PAIED.getCode();
        waybillOperateTrackService.insert(origin.getWaybillId(), OperateType.CONFIRM_PROJECT_BILL_FREIGHT,
                OperateApplication.CUSTOMER_SYS, buildTrackNotRequieParam(origin, remark),
                loginEmployee);
    }

    /**
     * 修改待支付运单税率
     *
     * @param waybillId
     * @param taxRateId
     * @param loginEmployee
     */
    @RequestMapping(value = "bill/{waybillId}/{taxRateId}/modifyBillTaxRate", method = RequestMethod.POST)
    @ResponseBody
    public void modifyWaybillTaxRate(@PathVariable(value = "waybillId") Integer waybillId, @PathVariable(value = "taxRateId") Integer taxRateId, LoginEmployee loginEmployee) {
        TaxRate taxRate = taxRateService.getTaxRate(taxRateId);
        if (null != taxRate) {
            filialeBillService.modifyWaybillTaxRate(waybillId, taxRate.getTaxRateValue(), loginEmployee);
        }
    }

    /**
     * 校验车辆需求
     *
     * @param projectBillVo
     */
//    @RequestMapping(value = "bill/checkRequire", method = RequestMethod.POST)
//    @ResponseBody
//    public void truckRequireCheckForUser(@RequestBody ProjectBillVo projectBillVo) {
//        filialeBillService.truckRequireCheckForUser(projectBillVo);
//    }

    /**
     * 检查项目下是否有运费规则
     *
     * @param projectId
     * @param truckTypeId
     */
//    @RequestMapping(value = "bill/{projectId}/{truckTypeId}/checkFreightRule", method = RequestMethod.POST)
//    @ResponseBody
//    public void checkTruckTypeHasFreightRule(@PathVariable(value = "projectId") Integer projectId, @PathVariable(value = "truckTypeId") Integer truckTypeId) {
//        filialeBillService.checkTruckTypeHasFreightRule(projectId, truckTypeId);
//    }

}
