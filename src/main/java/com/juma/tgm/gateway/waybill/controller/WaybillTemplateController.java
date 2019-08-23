package com.juma.tgm.gateway.waybill.controller;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.user.domain.LoginEcoUser;
import com.juma.tgm.driver.domain.Driver;
import com.juma.tgm.driver.domain.DriverLoginUser;
import com.juma.tgm.driver.service.DriverService;
import com.juma.tgm.gateway.common.BaseController;
import com.juma.tgm.gateway.waybill.controller.vo.WaybillVo;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillBo;
import com.juma.tgm.waybill.domain.WaybillDetailInfo;
import com.juma.tgm.waybill.service.DeliveryPointSupplementService;
import com.juma.tgm.waybill.service.WaybillParamService;
import com.juma.tgm.waybill.service.WaybillService;
import com.juma.tgm.waybillReport.service.WaybillReportService;

/**
 * 驹马app接口 运单模板返回
 *
 * @author weilibin
 */

@Controller
@RequestMapping("waybill/tpl")
public class WaybillTemplateController extends BaseController {

    private final Logger log = LoggerFactory.getLogger(WaybillTemplateController.class);
    @Resource
    private WaybillService waybillService;
    @Resource
    private DriverService driverService;
    @Resource
    private WaybillReportService waybillReportService;
    @Resource
    private WaybillParamService waybillParamService;

    @Resource
    private DeliveryPointSupplementService deliveryPointSupplementService;

    private ModelAndView buildView(String template, DriverLoginUser driverLoginUser) {
        ModelAndView modelAndView = new ModelAndView(template);
        this.buildDriverBaseInfo(modelAndView, driverLoginUser);
        return modelAndView;
    }

    /**
     * 运单池(待接单)(司机端：驹马生态用户使用)
     */
    @RequestMapping(value = "acceptable", method = RequestMethod.POST)
    public ModelAndView templateAcceptable(@RequestBody PageCondition pageCondition, DriverLoginUser driverLoginUser, LoginEcoUser driverLoginEcoUser) {
        log.debug("运单池(待接单) start");
        ModelAndView modelAndView = this.buildView("tpl/acceptable", driverLoginUser);
        pageCondition.getFilters().put("tenantId", driverLoginEcoUser.getTenantId());
        Page<WaybillBo> page = waybillService.getPageForAcceptableWaybillList(pageCondition, driverLoginEcoUser);
        log.debug("运单池(待接单) page获取完毕");
        modelAndView.addObject("pageObj", page);
        log.debug("运单池(待接单) end");
        return modelAndView;
    }

    /**
     * 运单详情(待接单)(司机端：驹马生态用户使用)
     */
    @RequestMapping(value = "acceptable/detail", method = RequestMethod.POST)
    public ModelAndView acceptableDetail(@RequestBody Waybill waybill, DriverLoginUser driverLoginUser, LoginEcoUser driverLoginEcoUser) {
        ModelAndView modelAndView = this.buildView("tpl/acceptable.detail", driverLoginUser);
        WaybillBo waybillBo = waybillService.findWaybillBo(waybill.getWaybillId(), driverLoginEcoUser);
        if (null != waybillBo && null != waybillBo.getWaybill() && null != waybillBo.getWaybill().getDriverId()) {
            Driver driver = driverService.findDriverByUserId(driverLoginEcoUser.getUserId());
            if (null != driver) {
                if (!driver.getDriverId().equals(waybillBo.getWaybill().getDriverId())) {
                    waybillBo.getWaybill().setSelfWaybill(false);
                }
            }
        }
        modelAndView.addObject("waybillBo", waybillBo);
        return modelAndView;
    }

    /**
     * 任务列表
     */
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public ModelAndView templateList(@RequestBody PageCondition pageCondition, DriverLoginUser driverLoginUser,
            LoginEcoUser driverLoginEcoUser) {
        ModelAndView modelAndView = this.buildView("tpl/task", driverLoginUser);

        if (driverLoginUser.getDriverId() == null) {
            modelAndView.addObject("pageObj", new Page<WaybillBo>(pageCondition.getPageNo(),
                    pageCondition.getPageSize(), 0, new ArrayList<WaybillBo>()));
            return modelAndView;
        }

        pageCondition.getFilters().put("driverId", driverLoginUser.getDriverId());
        pageCondition.getFilters().put("tenantId", driverLoginEcoUser.getTenantId());
        Page<WaybillBo> page = waybillService.getPageForTodoWaybillList(pageCondition, driverLoginEcoUser);
        modelAndView.addObject("pageObj", page);
        return modelAndView;
    }

    /**
     * 运单详情(配送中)(司机端：驹马生态用户使用)
     */
    @RequestMapping(value = "distribution", method = RequestMethod.POST)
    public ModelAndView distribution(@RequestBody Waybill waybill,DriverLoginUser driverLoginUser, LoginEcoUser driverLoginEcoUser) {
        ModelAndView modelAndView = this.buildView("tpl/distribution", driverLoginUser);
        modelAndView.addObject("waybillBo", waybillService.findWaybillBo(waybill.getWaybillId(), driverLoginEcoUser));
        return modelAndView;
    }

    /**
     * 根据运单ID得到运单详情(司机端：驹马生态用户使用)
     */
    @RequestMapping(value = "order/detail", method = RequestMethod.POST)
    public ModelAndView orderDetail(@RequestBody WaybillVo waybillVo,DriverLoginUser driverLoginUser, LoginEcoUser driverLoginEcoUser) {
        if (null == waybillVo.getWaybillId()) {
            throw new BusinessException("validationFailure", "errors.validation.failure");
        }
        ModelAndView modelAndView = this.buildView("tpl/order.detail", driverLoginUser);
        WaybillDetailInfo waybillInfo = waybillService.getWaybillInfo(waybillVo.getWaybillId(), driverLoginEcoUser);
        if (null != waybillInfo && null != waybillInfo.getWaybill() && null != waybillInfo.getWaybill().getDriverId()) {
            Driver driver = driverService.findDriverByUserId(driverLoginEcoUser.getUserId());
            if (null != driver) {
                if (!driver.getDriverId().equals(waybillInfo.getWaybill().getDriverId())) {
                    waybillInfo.getWaybill().setSelfWaybill(false);
                }
            }
        }
        //司机已阅读
        waybillParamService.driverReadWaybill(waybillVo.getWaybillId(), driverLoginEcoUser);
        // 埋点
        waybillService.saveWaybillViewHistory(waybillVo.getWaybillId(), driverLoginUser.getDriverId());
        modelAndView.addObject("waybillInfo", waybillInfo);
        modelAndView.addObject("pointCount", deliveryPointSupplementService.countDeliveryPointSupplement(waybillVo.getWaybillId()));
        return modelAndView;
    }

    /**
     * 司机端报表(司机端：驹马生态用户使用)
     */
    @ResponseBody
    @RequestMapping(value = "income/statistics", method = RequestMethod.POST)
    public ModelAndView income(@RequestBody PageCondition pageCondition,DriverLoginUser driverLoginUser, LoginEcoUser driverLoginEcoUser) {
        ModelAndView modelAndView = this.buildView("tpl/income.statistics", driverLoginUser);
        pageCondition.getFilters().put("client", "smartTruck");
        modelAndView.addObject("report", waybillReportService.getDriverReport(pageCondition, driverLoginUser));
        return modelAndView;
    }

    /**
     * 获取运单(不止是今天的数据)(司机端：驹马生态用户使用)
     */
    @RequestMapping(value = "todayWaybillInfo", method = RequestMethod.POST)
    public ModelAndView todayWaybillInfoTpl(DriverLoginUser driverLoginUser,
            LoginEcoUser driverLoginEcoUser) {
        ModelAndView modelAndView = this.buildView("tpl/index.info", driverLoginUser);
        modelAndView.addObject("waybillInfo", waybillService.getWaybillInfo(driverLoginEcoUser));
        return modelAndView;
    }

    /**
     * 获取运单(不止是今天的数据)，不通过登录，访问需要userId
     */
    // TODO WEI 调查是否使用
//    @RequestMapping(value = "notNeedLogin/todayWaybillInfo", method = RequestMethod.POST)
//    public ModelAndView todayWaybillInfoTpl(@RequestBody Driver driver) {
//        ModelAndView modelAndView = new ModelAndView("tpl/index.info");
//        if (null == driver) {
//            throw new BusinessException("notLogin", "errors.notLogin");
//        }
//        Integer userId = driver.getUserId();
//        if (null == userId) {
//            throw new BusinessException("notLogin", "errors.notLogin");
//        }
//        driver = driverService.findDriverByUserId(userId);
//        if (null == driver) {
//            throw new BusinessException("notLogin", "errors.notLogin");
//        }
//        DriverLoginUser driverLoginUser = new DriverLoginUser();
//        driverLoginUser.setDriverId(driver.getDriverId());
//        this.buildDriverBaseInfo(modelAndView, driver);
//        modelAndView.addObject("waybillInfo", waybillService.getWaybillInfo(driverLoginUser));
//        return modelAndView;
//    }

    /**
     * 待配送运单列表(不止是今天的数据)(司机端：驹马生态用户使用)
     */
    @RequestMapping(value = "todayList", method = RequestMethod.POST)
    public ModelAndView todayListTpl(DriverLoginUser driverLoginUser,
            LoginEcoUser driverLoginEcoUser, @RequestBody PageCondition pageCondition) {
        ModelAndView modelAndView = this.buildView("tpl/index.list", driverLoginUser);
        modelAndView.addObject("pageObj", waybillService.getTodayWaitList(pageCondition, driverLoginEcoUser));
        return modelAndView;
    }

    /**
     * 待配送运单列表(不止是今天的数据)，不通过登录，访问需要userId
     */
    // TODO WEI 调查是否使用
//    @RequestMapping(value = "notNeedLogin/todayList", method = RequestMethod.POST)
//    public ModelAndView todayListTpl(@RequestBody PageCondition pageCondition) {
//        ModelAndView modelAndView = new ModelAndView("tpl/index.list");
//        this.buildView(modelAndView, pageCondition);
//        return modelAndView;
//    }
//
//    // 构造view
//    private ModelAndView buildView(ModelAndView modelAndView, PageCondition pageCondition) {
//        Integer userId = getUserId(pageCondition);
//        if (null == userId) {
//            throw new BusinessException("notLogin", "errors.notLogin");
//        }
//        Driver driver = driverService.findDriverByUserId(userId);
//        if (null == driver) {
//            throw new BusinessException("notLogin", "errors.notLogin");
//        }
//        this.buildDriverBaseInfo(modelAndView, driver);
//        DriverLoginUser driverLoginUser = new DriverLoginUser();
//        driverLoginUser.setDriverId(driver.getDriverId());
//        modelAndView.addObject("pageObj", waybillService.getTodayWaitList(pageCondition, driverLoginUser));
//        return modelAndView;
//    }

    // 获取userId
    private Integer getUserId(PageCondition pageCondition) {
        Map<String, Object> filters = pageCondition.getFilters();
        if (null != filters) {
            Object obj = filters.get("userId");
            if (null != obj) {
                return Integer.valueOf(obj.toString());
            }
        }
        return null;
    }
}
