package com.juma.tgm.gateway.filiale.controller.v2;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.giants.common.tools.PageQueryCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.common.query.QueryCond;
import com.juma.tgm.gateway.common.BaseController;
import com.juma.tgm.operateLog.enumeration.OperateTypeEnum;
import com.juma.tgm.project.service.ProjectService;
import com.juma.tgm.project.service.RoadMapService;
import com.juma.tgm.project.vo.ProjectFilter;
import com.juma.tgm.project.vo.ProjectVoAndInfo;
import com.juma.tgm.project.vo.RoadMapVo;
import com.juma.tgm.project.vo.v2.ProjectParamVo;
import com.juma.tgm.project.vo.v2.ProjectVo;
import com.juma.tgm.project.vo.v2.ProjectVoApp;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @author : final
 * @version : 1.0
 * @date : 2018/10/9 0009 16:24
 * @Description : Copyright : Copyright (c) 2018 Company : my cloud
 * @link : com.juma.tgm.gateway.filiale.controller.v2.ProjectControllerV2
 */
@RestController
@RequestMapping("/project/v2")
public class ProjectControllerV2 extends BaseController {

    @Resource
    private ProjectService projectService;

    @Resource
    private RoadMapService roadMapService;

    /***
     *
     * 创建一个项目
     * 
     * @param projectFormAndInfo
     *            post 参数
     *
     * @param loginEmployee
     *            操作人相关信息
     *
     * @see ProjectVoAndInfo
     *
     * @see LoginEmployee
     *
     * @throws BusinessException;
     */
    @RequestMapping(method = RequestMethod.POST)
    public Integer create(@RequestBody ProjectVoAndInfo projectFormAndInfo, LoginEmployee loginEmployee)
            throws BusinessException {
        this.checkCreateOrUpdateForm(projectFormAndInfo);
        projectFormAndInfo.getProject().setProjectId(null);// 强制新增
        projectFormAndInfo.getProject().setManagerId(loginEmployee.getEmployeeId());
        Integer projectId = projectService.create(projectFormAndInfo, loginEmployee);
        super.insertLog(OperateTypeEnum.ADD_PROJECT, projectId, null, loginEmployee);
        return projectId;

    }

    /***
     *
     * 修改一个项目
     * 
     * @param projectFormAndInfo
     *            post 参数
     *
     * @param loginEmployee
     *            操作人相关信息
     *
     * @see ProjectVoAndInfo
     *
     * @see LoginEmployee
     *
     * @throws BusinessException;
     */
    @RequestMapping(method = RequestMethod.PUT)
    public Integer update(@RequestBody ProjectVoAndInfo projectFormAndInfo, LoginEmployee loginEmployee)
            throws BusinessException {
        this.checkCreateOrUpdateForm(projectFormAndInfo);
        projectFormAndInfo.getProject().setCustomerId(null);
        Integer projectId = projectService.update(projectFormAndInfo, loginEmployee);
        super.insertLog(OperateTypeEnum.MODIFY_PROJECT, projectFormAndInfo.getProject().getProjectId(), null,
                loginEmployee);
        return projectId;
    }

    /**
     *
     * 获取 项目详情
     *
     * @param projectId
     *            项目 id
     *
     * @param loginEmployee
     *            操作人相关信息
     *
     * @see LoginEmployee
     *
     * @throws BusinessException
     */
    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
    public ProjectVoAndInfo info(@PathVariable("projectId") Integer projectId, LoginEmployee loginEmployee)
            throws BusinessException {
        return projectService.info(projectId, loginEmployee);
    }

    /**
     * 获取项目 列表
     *
     * @param pageQueryCondition
     *            查询 参数
     *
     * @param loginEmployee
     *            登录人信息
     *
     * @see LoginEmployee
     *
     * @throws BusinessException
     *
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST) // 这里其实不规范，破窗效应
                                                                    // 而已
    public Page<ProjectVoApp> search(@RequestBody PageCondition pageQueryCondition, LoginEmployee loginEmployee)
            throws BusinessException {
		QueryCond<ProjectParamVo> projectParamVoQueryCond = buildQueryParam(pageQueryCondition, loginEmployee);
        return projectService.searchV2(projectParamVoQueryCond, loginEmployee);
    }

    /**
     * 检测 创建 或者修改时候 的参数 合法性
     */
    private void checkCreateOrUpdateForm(ProjectVoAndInfo projectFormAndResponseInfo) throws BusinessException {
        if (projectFormAndResponseInfo.getProject() == null) {
            throw new BusinessException("project", "缺少必要参数project 项目信息");
        } else if (CollectionUtils.isEmpty(projectFormAndResponseInfo.getRoadMapVos())) {
            throw new BusinessException("roads", "缺少必要参数roads 线路信息");
        } else {
            for (RoadMapVo road : projectFormAndResponseInfo.getRoadMapVos()) {
                Integer index = projectFormAndResponseInfo.getRoadMapVos().indexOf(road) + 1;
                if (road.getRoadMap() == null) {
                    throw new BusinessException("destAdress", "第" + index + "条路线基本信息为空");
                } else if (CollectionUtils.isEmpty(road.getListRoadMapSrcAdress())) {
                    throw new BusinessException("srcAdress", "第" + index + "条路线信息的取货地为空");
                } else if (CollectionUtils.isEmpty(road.getListRoadMapPriceRule())) {
                    throw new BusinessException("priceRuleList", "第" + index + "条路线信息的计价规则为空");
                }
            }
        }
    }

    @RequestMapping(value = "/road-map", method = RequestMethod.PUT)
    public Integer update(@RequestBody RoadMapVo roadMapVo, LoginEmployee loginEmployee) throws BusinessException {
        roadMapService.update(roadMapVo, loginEmployee);
        return 1;// 更新条数
    }

    @RequestMapping(value = "/road-map/{id}", method = RequestMethod.GET)
    public RoadMapVo roadMapInfo(@PathVariable("id") Integer id) throws BusinessException {
        return roadMapService.get(id);
    }

    @RequestMapping(value = "/findByPage", method = RequestMethod.POST)
    public Page<RoadMapVo> findRoadByPage(@RequestBody PageCondition pageQueryCondition, LoginEmployee loginEmployee) {
        return projectService.findRoadMapVoPage(pageQueryCondition, loginEmployee);
    }

    /**
     * 转换参数
     *
     * @param condition
     */
    private QueryCond<ProjectParamVo> buildQueryParam(PageCondition condition, LoginEmployee loginEmployee) {

		QueryCond<ProjectParamVo> projectQueryCond = new QueryCond<>();
		projectQueryCond.setFilters(new ProjectParamVo());
		projectQueryCond.setPageNo(condition.getPageNo());
		projectQueryCond.setPageSize(condition.getPageSize());

        Map<String, Object> param = condition.getFilters();
        if (MapUtils.isEmpty(param)) {
        	return projectQueryCond;
        }

        // 以下条件必须同时满足
        String type = null;
        try {
            type = param.get("type").toString();
        } catch (Exception e) {
            //
        } finally {
            param.remove("type");
        }
        // 判断参数是企业客户
        String custStr = null;
        try {
            custStr = param.get("customerId").toString();
        } catch (Exception e) {
            // return;
        } finally {
            param.remove("customerId");
        }
        if (param.get("projectStatusList") != null) {//项目下单时只筛选运行中的项目，项目管理列表筛选未启动、运行中、已暂停的项目
            projectQueryCond.getFilters().setProjectStatusList(JSON.parseArray(JSON.toJSONString(param.get("projectStatusList")), Integer.class));
        }
        if (StringUtils.isNumeric(custStr) && StringUtils.equals("1", type)) {
            param.put("customerId", Integer.valueOf(custStr));
			projectQueryCond.getFilters().setCustomerId(Integer.valueOf(custStr));
        } else if (StringUtils.isNumeric(custStr) && StringUtils.equals("2", type)) {
            param.put("truckCustomerId", Integer.valueOf(custStr));
			projectQueryCond.getFilters().setTruckCustomerId(Integer.valueOf(custStr));
        } else if (StringUtils.isNumeric(custStr) && StringUtils.equals("3", type)) {
            param.put("projectId", Integer.valueOf(custStr));
			projectQueryCond.getFilters().setProjectId(Integer.valueOf(custStr));
        }

        return projectQueryCond;
    }
}
