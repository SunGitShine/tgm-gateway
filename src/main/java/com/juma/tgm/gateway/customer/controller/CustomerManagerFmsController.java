package com.juma.tgm.gateway.customer.controller;

import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageQueryCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.fms.core.domain.BillDO;
import com.juma.fms.core.domain.BillFilter;
import com.juma.fms.core.domain.ReceiptBillInfoBo;
import com.juma.tgm.fms.service.v2.ReceiptFreightFeeService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 客户经理端 fms 模块
 *
 * @ClassName: CustomerManagerFmsController
 * @Description:
 * @author: liang
 * @date: 2018-08-08 16:08
 * @Copyright: 2018 www.jumapeisong.com Inc. All rights reserved.
 */
@RestController
@RequestMapping(value = "customerManager")
public class CustomerManagerFmsController {

    private static final Logger log = LoggerFactory.getLogger(CustomerManagerFmsController.class);

    @Resource
    private ReceiptFreightFeeService receiptFreightFeeService;

    public static class ReceiptBillPageCondition extends PageQueryCondition<BillFilter> {
        public ReceiptBillPageCondition() {
            super();
        }

    }





    @ApiOperation(value = "经纪人端收款单列表查询", notes = "收款状态：0-未收款 1-部分收款 2-已收款；排序：0-升序 1-降序")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "filters.customerName", value = "客户名称", required = false, dataType = "string", paramType = "body"),
        @ApiImplicitParam(name = "filters.receiptStatus", value = "收款状态", required = false, dataType = "string", paramType = "body"),
        @ApiImplicitParam(name = "orderSort", value = "排序", required = false, dataType = "string", paramType = "body")
    })
    /**
     * 获取收款单列表
     *
     * @param billFilterPageQueryCondition 条件：收款单状态，客户名称；排序：创建时间
     * @param loginEmployee                当前登录人
     * @return 收款单列表
     */
    @RequestMapping(value = "receiptBill/list", method = RequestMethod.POST)
    public Page<BillDO> searchBillForReceipt(@RequestBody ReceiptBillPageCondition billFilterPageQueryCondition, LoginEmployee loginEmployee) {
        if (billFilterPageQueryCondition == null)
            throw new BusinessException("paramNullError", "errors.required", "查询参数");

        BillFilter filter = billFilterPageQueryCondition.getFilters();
        if (filter == null) {
            filter = new BillFilter();
            billFilterPageQueryCondition.setFilters(filter);
        }
        Page<BillDO> result = receiptFreightFeeService.searchBillForReceipt(billFilterPageQueryCondition, loginEmployee);

        return result;
    }

    @ApiOperation(value = "收款单详情", notes = "收款单")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "no", value = "对账单号", required = false, dataType = "string", paramType = "body"),
        @ApiImplicitParam(name = "id", value = "收款单id", required = true, dataType = "string", paramType = "body"),
    })
    /**
     * 获取收款单详情
     *
     * @param billDO 对账单号，收款单id
     * @return
     */
    @RequestMapping(value = "receiptBill/detail", method = RequestMethod.POST)
    public ReceiptBillInfoBo findReceiptBillInfoBo(@RequestBody BillDO billDO) {
        if (billDO == null)
            throw new BusinessException("paramNullError", "errors.required", "查询参数");

        if (billDO.getId() == null && StringUtils.isBlank(billDO.getNo())) {
            if (StringUtils.isBlank(billDO.getNo()))
                throw new BusinessException("noNullError", "errors.required", "对账单号");

            if (billDO.getId() == null) throw new BusinessException("billIdNullError", "errors.required", "收款单id");
        }

        return receiptFreightFeeService.findReceiptBillInfoBo(billDO);
    }


}
