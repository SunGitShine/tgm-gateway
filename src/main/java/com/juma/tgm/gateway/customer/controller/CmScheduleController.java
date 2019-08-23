package com.juma.tgm.gateway.customer.controller;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.customerManager.domain.ManagerSchedule;
import com.juma.tgm.customerManager.domain.vo.ManagerScheduleVo;
import com.juma.tgm.customerManager.service.ManagerScheduleService;
import com.juma.tgm.gateway.common.BaseController;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.service.WaybillService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

/**
 * @ClassName: CustomerManagerScheduleController
 * @Description:
 * @author: liang
 * @date: 2017-06-16 11:20
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
@Controller
@RequestMapping("customerManagerSchedule")
public class CmScheduleController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(CmScheduleController.class);

    @Resource
    private ManagerScheduleService managerScheduleService;

    @Resource
    private WaybillService waybillService;

    /**
     * 获取未完成事项列表
     *
     * @param pageCondition
     * @param loginEmployee
     * @return
     */
    @RequestMapping("unHandledList")
    @ResponseBody
    public Map<String, Object> unHandledList(@RequestBody PageCondition pageCondition, LoginEmployee loginEmployee) {
        Map<String, Object> data = new HashMap<>();
        //分页获取数据
        Page<ManagerSchedule> datas = managerScheduleService.getUnhandledList(pageCondition, loginEmployee);

        if (CollectionUtils.isEmpty(datas.getResults())) {
            data.put("datas", new Page<ManagerScheduleVo>());
            data.put("count", 0);
            return data;
        }

        //未处理条数
        int count = managerScheduleService.unhandledCount(loginEmployee);

        List<ManagerScheduleVo> voList = new ArrayList<>();
        ManagerScheduleVo vo = null;
        for (ManagerSchedule ms : datas.getResults()) {
            vo = new ManagerScheduleVo();
            BeanUtils.copyProperties(ms, vo);
            //获取项目信息
            if (ms.getWaybillId() != null) {
                Waybill bill = waybillService.getWaybill(ms.getWaybillId());
                if (bill != null) {
                    vo.setProjectId(bill.getProjectId());
                }

            }

            voList.add(vo);
        }
        Page<ManagerScheduleVo> pageVo = this.convertPage(datas);
        pageVo.setResults(voList);

        data.put("datas", pageVo);
        data.put("count", count);
        data.put("CDate", new Date());

        return data;
    }

    /**
     * 标记为已处理
     *
     * @param id
     */
    @RequestMapping("{id}/doHandled")
    @ResponseBody
    public void doHandled(@PathVariable("id") Integer id, LoginEmployee loginEmployee) {
        managerScheduleService.Handled(id, loginEmployee);
    }

    private Page<ManagerScheduleVo> convertPage(Page<ManagerSchedule> pageOrigin) {
        Page<ManagerScheduleVo> pageVo = new Page<>();
        pageVo.setTotal(pageOrigin.getTotal());
        pageVo.setPageNo(pageOrigin.getPageNo());
        pageVo.setPageSize(pageOrigin.getPageSize());

        return pageVo;
    }

}
