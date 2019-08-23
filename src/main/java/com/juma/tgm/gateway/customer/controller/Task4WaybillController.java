package com.juma.tgm.gateway.customer.controller;

import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageQueryCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.customerManager.domain.Task4Waybill;
import com.juma.tgm.customerManager.domain.Task4WaybillReport;
import com.juma.tgm.customerManager.domain.vo.taskWaybill.*;
import com.juma.tgm.customerManager.service.Task4WaybillReportService;
import com.juma.tgm.customerManager.service.Task4WaybillService;
import com.juma.tgm.customerManager.service.TaskWaybillTemplateService;
import com.juma.tgm.waybill.domain.WaybillDetailInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @ClassName: Task4WaybillController
 * @Description:
 * @author: liang
 * @date: 2018-10-09 14:41
 * @Copyright: 2018 www.jumapeisong.com Inc. All rights reserved.
 */
@RestController
public class Task4WaybillController {

    @Resource
    private Task4WaybillService task4WaybillService;

    @Resource
    private TaskWaybillTemplateService taskWaybillTemplateService;

    @Resource
    private Task4WaybillReportService task4WaybillReportService;


    /**
     * 任务搜索
     */
    public static class Task4WaybillQueryCondition extends PageQueryCondition<Task4WaybillQueryVo> {
        public Task4WaybillQueryCondition() {
            super();
        }
    }


    /**
     * 任务报告搜索
     */
    public static class Task4WaybillReportQueryCondition extends PageQueryCondition<Task4WaybillReportQueryVo> {
        public Task4WaybillReportQueryCondition() {
            super();
        }
    }


    /**
     * 定时发单列表
     *
     * @param queryCondition
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "taskWaybill/searchTask", method = RequestMethod.POST)
    public Page<Task4WaybillListVo> searchTask(@RequestBody Task4WaybillQueryCondition queryCondition, LoginEmployee loginEmployee) {
        queryCondition.getFilters().setEmployeeId(loginEmployee.getEmployeeId());
        queryCondition.getFilters().setUserId(loginEmployee.getUserId());

        Page<Task4WaybillListVo> pageData = task4WaybillService.searchTask(queryCondition, loginEmployee);

        return pageData;
    }

    /**
     * 定时发单模板详情
     *
     * @param taskTemplateId
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "taskWaybillTemplate/{taskTemplateId}/templateDetail", method = RequestMethod.GET)
    public TaskWaybillTemplateDetailVo getTemplateDetail(@PathVariable(value = "taskTemplateId") Integer taskTemplateId, LoginEmployee loginEmployee) {
        TaskWaybillTemplateDetailVo detailVo = taskWaybillTemplateService.getDetail(taskTemplateId, loginEmployee);

        return detailVo;
    }

    /**
     * 详情页任务报告列表
     *
     * @param task4WaybillReportQueryCondition
     * @return
     */
    @RequestMapping(value = "taskWaybillReport/searchReportForView", method = RequestMethod.POST)
    public Page<Task4WaybillReportDetailVo> searchReportForView(@RequestBody Task4WaybillReportQueryCondition task4WaybillReportQueryCondition) {
        Page<Task4WaybillReportDetailVo> page = task4WaybillReportService.searchForView(task4WaybillReportQueryCondition);

        return page;
    }

    /**
     * 任务模板编辑
     *
     * @param taskWaybillTemplateCreateVo
     * @param loginEmployee
     */
    @RequestMapping(value = "taskWaybillTemplate/modify", method = RequestMethod.POST)
    public void modifyTaskWaybill(@RequestBody TaskWaybillTemplateCreateVo taskWaybillTemplateCreateVo, LoginEmployee loginEmployee) {
        taskWaybillTemplateService.updateTaskWaybill(taskWaybillTemplateCreateVo, loginEmployee);
    }

    /**
     * 任务模板新增
     *
     * @param taskWaybillTemplateCreateVo
     * @param loginEmployee
     */
    @RequestMapping(value = "taskWaybillTemplate/add", method = RequestMethod.POST)
    public void addTaskWaybill(@RequestBody TaskWaybillTemplateCreateVo taskWaybillTemplateCreateVo, LoginEmployee loginEmployee) {
        taskWaybillTemplateService.addTaskWaybill(taskWaybillTemplateCreateVo, loginEmployee);
    }

    /**
     * 发单报告聚合列表
     *
     * @param queryCondition
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "taskWaybillReport/overViewList", method = RequestMethod.POST)
    public Page<Task4WaybillReportCountVo> overViewList(@RequestBody Task4WaybillReportQueryCondition queryCondition, LoginEmployee loginEmployee) {
        queryCondition.getFilters().setEmployeeId(loginEmployee.getEmployeeId());
        Page<Task4WaybillReportCountVo> page = task4WaybillReportService.overViewList(queryCondition);

        //标记报告已读
        task4WaybillReportService.modifyReportReadStatus(null, loginEmployee);

        return page;
    }

    /**
     * 发单报告列表
     *
     * @param queryCondition
     * @param loginEmployee
     * @return
     */
    //----计数
    @RequestMapping(value = "taskWaybillReport/overViewCount", method = RequestMethod.POST)
    public Task4WaybillReportCountVo overViewCount(@RequestBody Task4WaybillReportQueryCondition queryCondition, LoginEmployee loginEmployee) {
        queryCondition.getFilters().setEmployeeId(loginEmployee.getEmployeeId());
        Task4WaybillReportCountVo countVo = task4WaybillReportService.overViewCount(queryCondition);

        return countVo;
    }

    //----列表
    @RequestMapping(value = "taskWaybillReport/searchForDetailList", method = RequestMethod.POST)
    public Page<Task4WaybillReportDetailVo> searchForDetailList(@RequestBody Task4WaybillReportQueryCondition queryCondition, LoginEmployee loginEmployee) {
        queryCondition.getFilters().setCreateUserId(loginEmployee.getUserId());
        queryCondition.getFilters().setEmployeeId(loginEmployee.getEmployeeId());
        Page<Task4WaybillReportDetailVo> pageData = task4WaybillReportService.searchForDetailList(queryCondition);

        return pageData;
    }

    /**
     * 删除定时发单
     *
     * @param taskWaybillTemplateId
     */
    @RequestMapping(value = "taskWaybillTemplate/{taskWaybillTemplateId}/del", method = RequestMethod.POST)
    public void delTaskWaybillTemplate(@PathVariable(value = "taskWaybillTemplateId") Integer taskWaybillTemplateId) {
        taskWaybillTemplateService.delTaskWaybillTemplate(taskWaybillTemplateId);
    }


    /**
     * 修改任务报告状态
     *
     * @param taskWaybillReportId
     * @param status
     * @param loginEmployee
     */
    @RequestMapping(value = "taskWaybillReport/{taskWaybillReportId}/{status}/modify", method = RequestMethod.POST)
    public void modifyTaskReportStatus(@PathVariable(value = "taskWaybillReportId") Integer taskWaybillReportId, @PathVariable(value = "status") Byte status, LoginEmployee loginEmployee) {
        Task4WaybillReport task4WaybillReport = new Task4WaybillReport();
        task4WaybillReport.setTaskStatus(status);
        task4WaybillReport.setTaskReportId(taskWaybillReportId);
        task4WaybillReportService.modifyReportStatus(task4WaybillReport, loginEmployee);
    }

    /**
     * 任务概览
     *
     * @param queryCondition
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "taskWaybill/taskCount", method = RequestMethod.POST)
    public Task4WaybillListCountVo taskCount(@RequestBody Task4WaybillQueryCondition queryCondition, LoginEmployee loginEmployee) {
        queryCondition.getFilters().setEmployeeId(loginEmployee.getEmployeeId());
        queryCondition.getFilters().setUserId(loginEmployee.getUserId());
        Task4WaybillListCountVo countVo = task4WaybillService.taskCount(queryCondition, loginEmployee);

        return countVo;
    }


    @RequestMapping(value = "taskWaybill/waybill-info/{reportId}", method = RequestMethod.GET)
    public WaybillDetailInfo getWaybillDetailInfo(@PathVariable("reportId") Integer reportId, LoginEmployee loginEmployee) {
        Task4WaybillReport task4WaybillReport = task4WaybillReportService.getTask4WaybillReport(reportId);
        if (task4WaybillReport == null) {
            throw new BusinessException("task4WaybillReport", "未知的reportId" + reportId);
        }

        Task4Waybill task4Waybill = task4WaybillService.get(task4WaybillReport.getTaskId());
        if (task4Waybill == null) throw new BusinessException("taskHasDeleted", "该定时任务已被删除");

        return taskWaybillTemplateService.getWaybillDetailInfo(task4Waybill.getTaskWaybillTemplateId(), loginEmployee);
    }
}
