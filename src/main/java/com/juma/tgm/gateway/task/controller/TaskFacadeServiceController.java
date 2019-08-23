package com.juma.tgm.gateway.task.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.conf.domain.ConfParamOption;
import com.juma.tgm.common.query.QueryCond;
import com.juma.tgm.common.task.TaskConstants;
import com.juma.tgm.common.vo.Page;
import com.juma.tgm.gateway.task.controller.vo.CancelTask;
import com.juma.tgm.gateway.task.controller.vo.ChangeCapacity;
import com.juma.tgm.gateway.task.controller.vo.DeliveryFiler;
import com.juma.tgm.gateway.task.controller.vo.TaskCalendar;
import com.juma.tgm.gateway.task.controller.vo.UpdateBillPeriod;
import com.juma.tgm.project.domain.v2.ProjectDepot;
import com.juma.tgm.task.domain.TaskAck;
import com.juma.tgm.task.domain.TaskFixedDelivery;
import com.juma.tgm.task.domain.ext.GroupTaskCalendar;
import com.juma.tgm.task.domain.vo.TaskScheduledVO;
import com.juma.tgm.task.dto.gateway.GroupTaskCalendarFilter;
import com.juma.tgm.task.dto.gateway.TaskFilter;
import com.juma.tgm.task.service.TaskFacadeService;
import com.juma.tgm.task.service.TaskScheduledService;
import com.juma.tgm.task.vo.gateway.GroupTaskCount;
import com.juma.tgm.task.vo.gateway.InviteRequest;
import com.juma.tgm.task.vo.gateway.NotDeliveryReasonSort;
import com.juma.tgm.task.vo.gateway.Task;
import com.juma.tgm.task.vo.gateway.TaskAckDetail;
import com.juma.tgm.task.vo.gateway.TaskAckPage;
import com.juma.tgm.task.vo.gateway.TaskCalendarByProject;
import com.juma.tgm.task.vo.gateway.TaskCalendarMaster;
import com.juma.tgm.task.vo.gateway.TaskDetail;
import com.juma.tgm.task.vo.gateway.TaskStatusCount;
import com.juma.tgm.task.vo.gateway.UpdateToNotDelivery;
import com.juma.tgm.task.vo.gateway.UpdateToRecoverDelivery;
import com.juma.tgm.tools.service.AuthCommonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value = "任务管理")
@RestController
@RequestMapping(value = "task")
public class TaskFacadeServiceController {

    @Resource
    private TaskFacadeService taskFacadeService;

    @Resource
    private TaskScheduledService taskScheduledService;

    @Resource
    private AuthCommonService authCommonService;

    @ApiOperation(value = "任务权限",notes = "任务管理")
    @RequestMapping(value = "{taskId}/permission",method = RequestMethod.GET)
    public void checkPermission(@PathVariable Integer taskId, LoginEmployee loginEmployee) {
        taskFacadeService.checkPermission(taskId,loginEmployee);
    }

    @ApiOperation(value = "任务状态分组数量",notes = "任务管理")
    @RequestMapping(value = "group",method = RequestMethod.GET)
    public List<GroupTaskCount> groupTaskCount(LoginEmployee loginEmployee) {
        return taskFacadeService.groupTaskCount(loginEmployee);
    }

    @ApiOperation(value = "任务列表",notes = "任务管理")
    @RequestMapping(value = "page",method = RequestMethod.POST)
    public Page<Task> taskPage (@RequestBody QueryCond<TaskFilter> query, LoginEmployee loginEmployee) {
        return taskFacadeService.pageOfTask(query,loginEmployee);
    }

    @ApiOperation(value = "修改账期",notes = "任务管理")
    @RequestMapping(value = "bill_period",method = RequestMethod.PUT)
    public void updateBillPeriod (@RequestBody UpdateBillPeriod billPeriod, LoginEmployee loginEmployee) {
        taskFacadeService.updateBillPeriod(billPeriod.getTaskId()
                ,billPeriod.getBillPeriod()
                ,billPeriod.getBillPeriodReason(),loginEmployee);
    }

    @ApiOperation(value = "取消任务",notes = "任务管理")
    @RequestMapping(value = "cancel",method = RequestMethod.PUT)
    public void cancelTask (@RequestBody CancelTask cancelTask, LoginEmployee loginEmployee) {
        taskFacadeService.cancelTask(cancelTask.getTaskId(),cancelTask.getCancelReason(),loginEmployee);
    }

    @ApiOperation(value = "任务派车",notes = "任务管理")
    @RequestMapping(value = "invite_vendor",method = RequestMethod.PUT)
    public void inviteVendor(@RequestBody InviteRequest inviteRequest, LoginEmployee loginEmployee) {
        taskFacadeService.inviteVendor(inviteRequest,loginEmployee);
    }

    @ApiOperation(value = "任务改派",notes = "任务管理")
    @RequestMapping(value = "change_vendor",method = RequestMethod.PUT)
    public void updateToRecoverDelivery(@RequestBody InviteRequest inviteRequest,LoginEmployee loginEmployee) {
        taskFacadeService.changeVendor(inviteRequest,loginEmployee);
    }

    @ApiOperation(value = "任务详情",notes = "任务管理")
    @RequestMapping(value = "{taskId}",method = RequestMethod.GET)
    public TaskDetail getTaskDetail(@PathVariable Integer taskId) {
        return taskFacadeService.getTaskDetail(taskId);
    }

    @ApiOperation(value = "任务日历",notes = "任务日历")
    @RequestMapping(value = "calendar",method = RequestMethod.POST)
    public TaskCalendarMaster getTaskCalendarMaster(@RequestBody TaskCalendar taskCalendar) {
        return taskFacadeService.getTaskCalendarMaster(taskCalendar.getTaskId(),taskCalendar.getStartDate(),taskCalendar.getIsIncludeHeader());
    }

    @ApiOperation(value = "不配送",notes = "任务日历")
    @RequestMapping(value = "not_delivery",method = RequestMethod.PUT)
    public void updateToNotDelivery(@RequestBody UpdateToNotDelivery updateToNotDelivery,LoginEmployee loginEmployee) {
        taskFacadeService.updateToNotDelivery(updateToNotDelivery.getCalendarId()
                ,updateToNotDelivery.getReasonSort()
                ,updateToNotDelivery.getReason(),loginEmployee);
    }

    @ApiOperation(value = "恢复配送",notes = "任务日历")
    @RequestMapping(value = "recover_delivery",method = RequestMethod.PUT)
    public void updateToRecoverDelivery(@RequestBody UpdateToRecoverDelivery updateToRecoverDelivery,LoginEmployee loginEmployee) {
        taskFacadeService.updateToRecoverDelivery(updateToRecoverDelivery.getCalendarId(),updateToRecoverDelivery.getDriverId()
                ,updateToRecoverDelivery.getTruckId()
                ,updateToRecoverDelivery.getReason(),loginEmployee);
    }

    @ApiOperation(value = "创建任务",notes = "创建任务")
    @RequestMapping(value = "create",method = RequestMethod.POST)
    public Integer createTaskScheduled(@RequestBody TaskScheduledVO taskScheduledVO,LoginEmployee loginEmployee) {
        return taskScheduledService.createTaskScheduled(taskScheduledVO, loginEmployee);
    }


    @ApiOperation(value = "更换运力前的冲突检查",notes = "任务日历")
    @RequestMapping(value = "conflict_change_capacity",method = RequestMethod.POST)
    public int conflictChangeCapacity(@RequestBody ChangeCapacity changeCapacity,LoginEmployee loginEmployee) {
        return taskFacadeService.conflictChangeCapacity(changeCapacity.getCalendarId()
                ,changeCapacity.getDriverId(),changeCapacity.getTruckId(),changeCapacity.getType(),loginEmployee);
    }

    @ApiOperation(value = "更换运力",notes = "任务日历")
    @RequestMapping(value = "change_capacity",method = RequestMethod.PUT)
    public void changeCapacity(@RequestBody ChangeCapacity changeCapacity,LoginEmployee loginEmployee) {
        taskFacadeService.doChangeCapacity(changeCapacity.getCalendarId()
                ,changeCapacity.getDriverId(),changeCapacity.getTruckId(),changeCapacity.getType(),loginEmployee);
    }

    @ApiOperation(value = "不配送原因",notes = "任务日历")
    @RequestMapping(value = "not_delivery_reason",method = RequestMethod.GET)
    public NotDeliveryReasonSort notDeliveryReasonSort() {
        return taskFacadeService.notDeliveryReasonSort();
    }

    @ApiOperation(value = "是否有待回复", notes = "任务改派")
    @RequestMapping(value = "have_wait_back/{taskId}",method = RequestMethod.GET)
    public Boolean haveWaitBack(@PathVariable Integer taskId){
        return taskScheduledService.haveWaitBack(taskId);
    }

    @ApiOperation(value = "任务邀请列表",notes = "任务详情")
    @RequestMapping(value = "ack/page",method = RequestMethod.POST)
    public Page<TaskAckPage> taskAckPage (@RequestBody QueryCond<TaskAck> queryCond, @ApiParam(hidden = true)LoginEmployee loginEmployee) {
        return taskScheduledService.findTaskAckPage(queryCond,loginEmployee);
    }

    @ApiOperation(value = "任务邀请详情", notes = "任务详情")
    @RequestMapping(value = "task_ack_detail/{taskAckId}",method = RequestMethod.GET)
    public TaskAckDetail haveWaitBack(@PathVariable Integer taskAckId, @ApiParam(hidden = true)LoginEmployee loginEmployee){
        return taskScheduledService.findTaskAckDetail(taskAckId, loginEmployee);
    }

    @ApiOperation(value = "承运商结算账期列表", notes = "承运商结算账期列表")
    @RequestMapping(value = "vendor_period/list",method = RequestMethod.GET)
    public List<ConfParamOption> vendorPeriodList(){
        return authCommonService.listOption(TaskConstants.VENDOR_ACCOUNT_PERIOD);
    }

	@ApiOperation(value = "项目仓库列表", notes = "创建任务")
	@RequestMapping(value = "project_depot/list",method = RequestMethod.GET)
	public List<ProjectDepot> projectDepotList(@RequestParam Integer projectId){
		return taskScheduledService.findDepotByProjectId(projectId);
	}

    @ApiOperation(value = "获取配置数据", notes = "获取配置数据"
        + "运力冲突有效冗余：transport_conflict_valid_redundant"
        + "任务最大预开始日期: tax_max_pre_start_date"
        + "任务有效期最大天数：tax_max_valid_day"
        + "运单最长生成天数：waybill_max_create_day"
        + "时段冲突允许通过天数比例: time_conflict_day_percent"
        + "时段冲突允许通过天数：time_conflict_day")
    @RequestMapping(value = "config/list",method = RequestMethod.GET)
    public Integer vendorPeriodList(@RequestParam String configKey){
        List<ConfParamOption> confParamOptions = authCommonService.listOption(configKey);
        if(!confParamOptions.isEmpty()){
            String value = confParamOptions.get(0).getOptionValue();
            return Integer.parseInt(value);
        }
        return null;
    }

    @ApiOperation(value = "配送点联系人信息",notes = "选择配送点")
    @RequestMapping(value = "select/delivery",method = RequestMethod.POST)
    public List<TaskFixedDelivery> pageOfDelivery (@RequestBody DeliveryFiler filter, LoginEmployee loginEmployee) {
        return taskScheduledService.findDeliveryByFiler(filter.getProjectId(), filter.getLinkName(), filter.getBackPageSize(), loginEmployee);
    }

    @ApiOperation(value = "项目不同状态的任务数量", notes = "项目日历")
    @RequestMapping(value = "task_status/count",method = RequestMethod.GET)
    public TaskStatusCount taskStatusCount(@RequestParam Integer projectId){
        return taskScheduledService.taskNumByProject(projectId);
    }

    @ApiOperation(value = "统计任务日历数量", notes = "项目日历"
        + "projectId必传，startTime必传（格式2019-08-07 00:00:00），endTime必传（格式2019-08-07 23:59:59）")
    @RequestMapping(value = "group_calendar/count",method = RequestMethod.POST)
    public List<GroupTaskCalendar> taskStatusCount(@RequestBody GroupTaskCalendarFilter filter, @ApiParam(hidden = true)LoginEmployee loginEmployee){
        return taskScheduledService.groupTaskCalendarCount(filter);
    }

    @ApiOperation(value = "获取日历数据", notes = "项目日历"
        + "projectId必传，workStatus必传（当未签到、已到仓、已离仓时传0），"
        + "startTime必传（格式2019-08-07 00:00:00），endTime必传（格式2019-08-07 23:59:59）")
    @RequestMapping(value = "calendar/list",method = RequestMethod.POST)
    public List<TaskCalendarByProject> getTaskCalendarBy(@RequestBody GroupTaskCalendarFilter filter, @ApiParam(hidden = true)LoginEmployee loginEmployee){
        return taskScheduledService.getTaskCalendarBy(filter);
    }
}
