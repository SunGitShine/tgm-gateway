/**
 *
 */
package com.juma.tgm.gateway.common;

import com.alibaba.fastjson.JSON;
import com.giants.cache.redis.RedisClient;
import com.juma.auth.conf.domain.BusinessAreaNode;
import com.juma.auth.user.domain.LoginUser;
import com.juma.conf.domain.ConfParamOption;
import com.juma.conf.service.ConfParamService;
import com.juma.tgm.common.BaseUtil;
import com.juma.tgm.common.Constants;
import com.juma.tgm.driver.domain.Driver;
import com.juma.tgm.driver.domain.DriverBaseInfo;
import com.juma.tgm.driver.domain.DriverLoginUser;
import com.juma.tgm.driver.service.DriverService;
import com.juma.tgm.operateLog.domain.OperateLog;
import com.juma.tgm.operateLog.enumeration.LogSignEnum;
import com.juma.tgm.operateLog.enumeration.OperateApplicatoinEnum;
import com.juma.tgm.operateLog.enumeration.OperateTypeEnum;
import com.juma.tgm.operateLog.service.OperateLogService;
import com.juma.tgm.truck.service.TruckService;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillOperateTrackNotRequieParam;
import com.juma.tgm.waybill.service.WaybillService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;

/**
 * @author vencent.lu
 *         <p/>
 *         Create Date:2014年2月24日
 */
@Controller
public class BaseController {

    private final Logger log = LoggerFactory.getLogger(BaseController.class);
    @Resource
    private RedisClient redisClient;
    @Resource
    private DriverService driverService;
    @Resource
    private TruckService truckService;
    @Resource
    private WaybillService waybillService;
    @Resource
    private ConfParamService confParamService;
    @Resource
    private OperateLogService operateLogService;

    public void buildDriverBaseInfo(ModelAndView model, DriverLoginUser user) {
        log.debug("BaseController-->DriverLoginUser",
                user == null ? "DriverLoginUser is null" : JSON.toJSONString(user));
        if (null == model) {
            model = new ModelAndView();
        }
        DriverBaseInfo info = new DriverBaseInfo();
        if (null != user.getLoginEcoUser()) {
            Driver driver = driverService.findDriverByUserId(user.getLoginEcoUser().getUserId());
            this.buildInfo(info, driver, user.getLoginEcoUser());
        }
        info = waybillService.buildOngoingWaybill(info, user.getLoginEcoUser());
        model.addObject("driverBaseInfo", info);
    }

    // 构建基础信息
    private DriverBaseInfo buildInfo(DriverBaseInfo info, Driver driver, LoginUser loginUser) {
        Integer userId = driver.getUserId();
        info.setIsAcceptAllocateOrders(driver.getWhetherAcceptAllocateOrder());
        info.setWaybillIdNeedConfirmCeivedFreight(getWaybillId(Constants.APP_USER_CONFIRM_RECEIVED_FREIGHT + userId + loginUser.getTenantId()));
        Integer waybillId = getWaybillId(Constants.APP_USER_PRVEFIEX + Constants.STAR_DRIVER + userId + loginUser.getTenantId());
        info.setWaybillIdNeedToEvaluate(waybillId);
        info.setDriverStatus(driver.getStatus());
        info.setNickname(driver.getNickname());
        info.setContactPhone(driver.getContactPhone());
        return info;
    }

    // 获取waybillId
    private Integer getWaybillId(String key) {
        Serializable serializable = redisClient.get(key);
        if (null != serializable) {
            return BaseUtil.getInt(String.valueOf(serializable));
        }
        return 0;
    }

    /**
     * 获取用户中新配置信息
     *
     * @param key
     * @return
     */
    protected List<ConfParamOption> findConfig(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return confParamService.findParamOptions(key);
    }

    /**
     * 获取配置项
     * 
     * @param key
     * @param code
     * @return
     */
    protected ConfParamOption getConfigValue(String key, String code) {
        if (StringUtils.isBlank(key)) {
            return null;
        }

        if (StringUtils.isBlank(code)) {
            return null;
        }

        return confParamService.findParamOption(key, code);
    }

    protected void parallelList(List<BusinessAreaNode> areaNodeTree, Set<BusinessAreaNode> rows) {
        if (areaNodeTree == null) {
            return;
        }
        // 深度优先算法
        Deque<BusinessAreaNode> dfs = new LinkedList<>();
        for (BusinessAreaNode areaNode : areaNodeTree) {
            dfs.push(areaNode);
        }
        while (!dfs.isEmpty()) {
            BusinessAreaNode node = dfs.pop();
            if (CollectionUtils.isEmpty(node.getChildren())) {
                rows.add(node);
            }
            if (CollectionUtils.isNotEmpty(node.getChildren())) {
                List<BusinessAreaNode> nodeLeafs = node.getChildren();
                if (CollectionUtils.isEmpty(nodeLeafs)) {
                    continue;
                }
                for (BusinessAreaNode n : nodeLeafs) {
                    n.setAreaName(node.getAreaName() + "-" + n.getAreaName());
                    dfs.add(n);
                }
            }
        }
    }

    // 构造操作轨迹非必填参数
    protected WaybillOperateTrackNotRequieParam buildTrackNotRequieParam(Waybill waybill, String remark) {
        WaybillOperateTrackNotRequieParam notRequieParam = new WaybillOperateTrackNotRequieParam();
        notRequieParam.setCoordinate(waybill.getLocation());
        notRequieParam.setDeviceNo(waybill.getDeviceNo());
        notRequieParam.setDeviceType(waybill.getDeviceType());
        notRequieParam.setRemark(remark);
        return notRequieParam;
    }

    /**
     * 运单前端状态枚举名称转数字code
     *
     * @param codeStr
     * @return
     */
    protected List<Integer> waybillStatusViewStr2Code(List<String> codeStr) {

        if (CollectionUtils.isEmpty(codeStr)) {
            return null;
        }

        List<Integer> statusList = new ArrayList<>();
        for (String str : codeStr) {
            statusList.add(Enum.valueOf(Waybill.StatusView.class, str).getCode());
        }
        return statusList;

    }

    /**
     * 添加操作记录
     */
    protected void insertLog(OperateTypeEnum operateTypeEnum, Integer relationTableId, String remark,
            LoginUser loginUser) {
        OperateLog log = new OperateLog();
        log.setLogSign(LogSignEnum.PROJECT.getCode());
        log.setOperateType(operateTypeEnum.getCode());
        log.setOperateApplicatoin(OperateApplicatoinEnum.CUSTOMER_APP.getCode());
        log.setRelationTableId(relationTableId);
        log.setRemark(remark);

        operateLogService.insertByDubboAsync(log, loginUser);
    }
}
