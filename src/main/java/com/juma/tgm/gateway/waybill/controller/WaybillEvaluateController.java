package com.juma.tgm.gateway.waybill.controller;

import com.giants.common.exception.BusinessException;
import com.juma.tgm.waybill.domain.Waybill;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * 评价
 *
 * @author weilibin
 */

@Deprecated
@Controller
@RequestMapping("waybillEvaluate")
public class WaybillEvaluateController {

    /**
     * 对司机评价的信息页
     */
    @RequestMapping(value = "{waybillId}/evaluteToDriver/info", method = RequestMethod.POST)
    @ResponseBody
    public void evaluteToDriverIno(@PathVariable Integer waybillId) {
    }

    /**
     * 货主端：对司机评价
     */
    @RequestMapping(value = "customer/evaluteToDriver", method = RequestMethod.POST)
    @ResponseBody
    public void customerEvaluteToDriver() {
    }

    /**
     * 客户经理端：对司机评价
     */
    @RequestMapping(value = "manager/evaluteToDriver", method = RequestMethod.POST)
    @ResponseBody
    public void managerEvaluteToDriver() {
    }

    /**
     * 对用车人评价的信息页
     * 备注：由于使用方包含用车人端、客户经理端，故本期不能登录，以后更改
     */
    @RequestMapping(value = "{waybillId}/evaluteToCustomer/info", method = RequestMethod.POST)
    @ResponseBody
    public void evaluteToCustomerInfo(@PathVariable Integer waybillId) {
    }

    /**
     * 对用车人评价的信息页(模板返回)
     */
    @RequestMapping(value = "tpl/evaluteToCustomer/info", method = RequestMethod.POST)
    public ModelAndView evaluteToCustomerInfo(@RequestBody Waybill waybill) {
        if (null == waybill) {
            throw new BusinessException("validationFailure", "errors.validation.failure");
        }
        ModelAndView modelAndView = new ModelAndView("tpl/evaluate");
        return modelAndView;
    }

    /**
     * 对用车人评价
     */
    @RequestMapping(value = "evaluteToCustomer", method = RequestMethod.POST)
    @ResponseBody
    public void evaluteToCustomer() {
    }
}
