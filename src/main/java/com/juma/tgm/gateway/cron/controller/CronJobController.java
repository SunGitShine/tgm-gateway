package com.juma.tgm.gateway.cron.controller;

import com.juma.tgm.common.DateUtil;
import com.juma.tgm.cron.service.CronjobService;
import com.juma.tgm.customerManager.service.Task4WaybillService;
import com.juma.tgm.landing.waybill.service.DispatchingTruckService;
import com.juma.tgm.project.service.ProjectProcessService;
import com.juma.tgm.redis.service.TemperatureAlertService;
import com.juma.tgm.task.service.TaskScheduledService;
import com.juma.tgm.waybill.service.WaybillCronService;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("cron")
public class CronJobController {

    private final Logger log = LoggerFactory.getLogger(CronJobController.class);

    @Resource
    private CronjobService cronjobService;

    @Resource
    private WaybillCronService waybillCronService;

    @Resource
    private DispatchingTruckService dispatchingTruckService;

    @Resource
    private TemperatureAlertService temperatureAlertService;

    @Resource
    private Task4WaybillService task4WaybillService;

    @Resource
    private ProjectProcessService projectProcessService;

    @Resource
    private TaskScheduledService taskScheduledService;


    @ResponseBody
    @RequestMapping(value = "xiongying/addCache", method = RequestMethod.GET)
    public void addCache() {
        task4WaybillService.addRedisCache();
    }
    
    
    
    /**
     * @throws
     * @Title: temperatureAlert
     * @Description: 温控  每分钟执行
     * @param:
     * @return: void
     */
    @ResponseBody
    @RequestMapping(value = "temperatureAlert", method = RequestMethod.GET)
    public void temperatureAlert() {
        log.info("temperatureAlert start");
        temperatureAlertService.scan();
    }

    @ResponseBody
    @RequestMapping(value = "temperatureAlert/view", method = RequestMethod.GET)
    public String temperatureAlertView() {
        return temperatureAlertService.viewRedisToString();
    }


    /**
     * 落地配 司机没有确认
     */
    @ResponseBody
    @RequestMapping(value = "doNoDriverAnswerWaybill", method = RequestMethod.GET)
    public void doNoDriverAnswerWaybill() {
        dispatchingTruckService.doNoDriverAnswerWaybill();
    }

    /**
     * 用车时间提醒
     */
    @ResponseBody
    @RequestMapping(value = "planDeliveryTimeRemind", method = RequestMethod.GET)
    public void planDeliveryTimeRemind() {
        cronjobService.planDeliveryTimeRemind();
    }

    /**
     * 客户经理超期运费统计 每天凌晨4:00执行
     */
    @Deprecated
    @ResponseBody
    @RequestMapping(value = "customerManagerFreight", method = RequestMethod.GET)
    public void customerManagerFreight() {
        log.info("客户经理超期运费统计已停用");
    }

    /**
     * 更改每周KPI 每周周一凌晨00:30分执行
     */
    @ResponseBody
    @RequestMapping(value = "updateEveryWeekKpi", method = RequestMethod.GET)
    public void updateEveryWeekKpi() {
        log.info("更改每周KPI已停用");
    }

    /**
     * 重新计算昨天已完成运单的实际里程
     */
    @ResponseBody
    @RequestMapping(value = "updateYesActualMileage", method = RequestMethod.GET)
    public void UpdateYesActualMileage(String startTime, String endTime) {
        if (StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)) {
            return;
        }

        try {
            waybillCronService.cronUpdateYesActualMileage(DateUtil.parse(startTime, DateUtil.YYYYMMDD),
                DateUtil.dayEndReturnDate(DateUtil.parse(endTime, DateUtil.YYYYMMDD)));
        } catch (Exception e) {
        }
    }

    /**
     * 强制结束运单：暂未使用
     */
    @ResponseBody
    @RequestMapping(value = "cronConstraintFinishWaybill", method = RequestMethod.GET)
    public void cronConstraintFinishWaybill() {
        try {
            waybillCronService.cronConstraintFinishWaybill();
        } catch (Exception e) {
        }
    }

    /**
     * 固定需求自动建单
     */
    @RequestMapping(value = "fixedDemand/autoCreateBill", method = RequestMethod.POST)
    @ResponseBody
    public void autoCreateBill() {
//        filialeBillService.autoCreateBillTask();
        cronjobService.task4Waybill();
    }

    /**
     * 运输报告短信
     * 具体执行时间未确定
     */
    @RequestMapping(value = "transportReport/sms", method = RequestMethod.GET)
    @ResponseBody
    public void transportReportForTruckCustomerSms() {
        cronjobService.transportReportForTruckCustomerSms();
    }

    /**
     * 指定固定需求id建单
     */
    @RequestMapping(value = "fixedDemand/pointCreateBill", method = RequestMethod.POST)
    @ResponseBody
    public void pointCreateBill(@RequestBody List<Integer> fixedDemandIds) {
//        filialeBillService.createBillByFixedDemandId(fixedDemandIds);
    }

    /**
     * 迁移固定需求
     */
    @RequestMapping(value = "fixedDemandTransform/doTransform/{flag}", method = RequestMethod.POST)
    @ResponseBody
    public void transformFixedDemandData(@PathVariable(value = "flag") Boolean flag) {
        cronjobService.transformFixedDemandData(flag);
    }

    /**
     * 更新项目状态定时任务
     * 到时间过期的项目，未到执行时间的审批流程
     */
    @RequestMapping(value = "project/updateStatusTimer", method = RequestMethod.POST)
    @ResponseBody
    public void updateStatusTimer(){
        projectProcessService.updateProjectStatusTimer();
    }

    /**
     * 定时任务更新任务已过期和承运商邀请已失效
     * 10分钟执行一次
     */
    @RequestMapping(value = "task/updateTaskStatusTimer", method = RequestMethod.POST)
    @ResponseBody
    public void updateTaskStatusTimer(){
        taskScheduledService.updateTaskStatusTimer();
    }

    /**
     * 通过任务定时生成运单
     * 12小时执行一次
     */
    @RequestMapping(value = "task/createWaybillTimer", method = RequestMethod.POST)
    @ResponseBody
    public void createWaybillTimer(){
        taskScheduledService.executeTaskCreateWaybillTimer();
    }


    /**
     * 生成日报，根据运行中项目生成
     * 每天0点10分执行
     */
    @RequestMapping(value = "projectDaily/produce", method = RequestMethod.POST)
    @ResponseBody
    public void produceDailyReport(){
        cronjobService.produceDailyReport();
    }


    /**
     * 日报过期
     * 每天0点30执行
     */
    @RequestMapping(value = "projectDaily/expire", method = RequestMethod.POST)
    @ResponseBody
    public void expireDailyReport(){
        cronjobService.expireDailyReport();
    }


    /**
     * 日报状态变更，部分确认-->已确认
     * 每天0点20执行
     */
    @RequestMapping(value = "projectDaily/confirme", method = RequestMethod.POST)
    @ResponseBody
    public void confirmeAll(){
        cronjobService.confirmeAll();
    }


    /**
     * 删除无运单日报
     * 每天0点40执行
     */
    @RequestMapping(value = "projectDaily/delete", method = RequestMethod.POST)
    @ResponseBody
    public void deleteInvalidDaily(@RequestParam(required = false, value = "date") Date date){
        cronjobService.cleanInvalidDaily(date);
    }
}
