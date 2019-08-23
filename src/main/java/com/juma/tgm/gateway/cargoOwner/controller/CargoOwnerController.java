package com.juma.tgm.gateway.cargoOwner.controller;

import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.user.domain.LoginEcoUser;
import com.juma.auth.user.domain.User;
import com.juma.auth.user.domain.UserInfo;
import com.juma.auth.user.domain.UserVerification;
import com.juma.auth.user.service.UserService;
import com.juma.auth.user.service.UserVerificationService;
import com.juma.tgm.common.IDValidator.IDValidator;
import com.juma.tgm.customer.domain.CargoOwnerLoginUser;
import com.juma.tgm.customer.domain.vo.CargoOwnerUserInfo;
import com.juma.tgm.gateway.cargoOwner.vo.ScatteredWaybillCargoOwnerDetailVo;
import com.juma.tgm.gateway.cargoOwner.vo.ScatteredWaybillCargoOwnerVo;
import com.juma.tgm.gateway.cargoOwner.vo.UserVerifyVo;
import com.juma.tgm.gateway.common.BaseController;
import com.juma.tgm.scatteredWaybill.service.ScatteredWaybillService;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillDetailInfo;
import com.juma.tgm.waybill.domain.vo.ScatteredWaybillCreateVo;
import com.juma.tgm.waybill.domain.vo.ScatteredWaybillViewVo;
import com.juma.tgm.waybill.domain.vo.WaybillStatisticsParamVo;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateApplication;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateType;
import com.juma.tgm.waybill.service.WaybillOperateTrackService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 货主端Controller
 *
 * @ClassName: CargoOwnerController
 * @Description:
 * @author: liang
 * @date: 2017-11-14 14:38
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
@RequestMapping("/cargoOwner")
@RestController
public class CargoOwnerController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(CargoOwnerController.class);

    @Resource
    private ScatteredWaybillService scatteredWaybillService;

    @Resource
    private WaybillOperateTrackService waybillOperateTrackService;

    @Resource
    private UserService userService;

    @Resource
    private UserVerificationService userVerificationService;


    /**
     * 货主基础信息获取
     *
     * @param cargoOwnerLoginEcoUser
     * @return
     */
    @RequestMapping("userInfo")
    public CargoOwnerUserInfo getUserInfo(CargoOwnerLoginUser cargoOwnerLoginUser, LoginEcoUser cargoOwnerLoginEcoUser) {
        //用户中心数据
        UserInfo info = userService.findUserInfo(cargoOwnerLoginEcoUser.getUserId());
        if (info != null) {
            info.setPassword(null);
        }
        //运单数据
        WaybillStatisticsParamVo waybillStatisticsParamVo = new WaybillStatisticsParamVo();
        //所属货主
        waybillStatisticsParamVo.put("truckCustomerId", cargoOwnerLoginUser.getTruckCustomerId());
        //运单状态
        List<Integer> statusViewList = Arrays.asList(new Integer[]{Waybill.StatusView.FINISH.getCode(), Waybill.StatusView.WATING_PAY.getCode(), Waybill.StatusView.DELIVERYING.getCode(), Waybill.StatusView.WATING_DELIVERY.getCode()});
        waybillStatisticsParamVo.put("statusViewList", statusViewList);
        CargoOwnerUserInfo cargoOwnerUserInfo = new CargoOwnerUserInfo();
        cargoOwnerUserInfo.setUserInfo(info);

        return cargoOwnerUserInfo;
    }


    /**
     * 修改货主基础信息
     *
     * @param newUser
     * @param cargoOwnerLoginEcoUser
     */
    @RequestMapping("updateUserInfo")
    public void updateUserInfo(@RequestBody User newUser, LoginEcoUser cargoOwnerLoginEcoUser) {
        User user = userService.loadUser(cargoOwnerLoginEcoUser.getUserId());
        user.setPassword(null);
        user.setName(newUser.getName());
        user.setIcon(newUser.getIcon());
        userService.updateUser(user, cargoOwnerLoginEcoUser);
    }

    /**
     * 运单列表
     *
     * @param pageCondition
     * @param cargoOwnerLoginEcoUser
     * @return
     */
    @RequestMapping("waybill/search")
    public Page<ScatteredWaybillCargoOwnerVo> scatteredWaybillList(@RequestBody PageCondition pageCondition, CargoOwnerLoginUser cargoOwnerLoginUser, LoginEcoUser cargoOwnerLoginEcoUser) {
        if (cargoOwnerLoginUser == null)
            throw new BusinessException("truckCustomerError", "errors.common.prompt", "货主不存在");

        //客户端通过状态参数获取运单列表
        pageCondition.getFilters().put("truckCustomerId", cargoOwnerLoginUser.getTruckCustomerId());
        this.buildSearchParam(pageCondition);

        Page<ScatteredWaybillViewVo> pageData = scatteredWaybillService.searchForApp(pageCondition, cargoOwnerLoginEcoUser);
        //建单人不是自己则为平台代发
        return this.formatViewData(pageData, cargoOwnerLoginUser);
    }

    private Page<ScatteredWaybillCargoOwnerVo> formatViewData(Page<ScatteredWaybillViewVo> pageData, CargoOwnerLoginUser cargoOwnerLoginUser) {
        Page<ScatteredWaybillCargoOwnerVo> finalData = new Page<>(pageData.getPageNo(), pageData.getPageSize(), pageData.getTotal());
        if (CollectionUtils.isEmpty(pageData.getResults())) return finalData;

        List<ScatteredWaybillCargoOwnerVo> finalResult = new ArrayList<>();
        ScatteredWaybillCargoOwnerVo cargoOwnerVo = null;
        for (ScatteredWaybillViewVo vo : pageData.getResults()) {
            cargoOwnerVo = new ScatteredWaybillCargoOwnerVo();
            cargoOwnerVo.setScatteredWaybillViewVo(vo);
            cargoOwnerVo.setUserId(cargoOwnerLoginUser.getLoginEcoUser().getUserId());

            finalResult.add(cargoOwnerVo);
        }

        finalData.setResults(finalResult);
        return finalData;
    }

    /**
     * 参数格式化
     *
     * @param pageCondition
     */
    private void buildSearchParam(PageCondition pageCondition) {
        Map<String, Object> params = pageCondition.getFilters();

        if (params == null) {
            params = new HashMap<>();
            pageCondition.setFilters(params);
        }

        //状态code转换
        List<Integer> codes = null;
        try {
            List<String> codeStr = (List<String>) params.get("statusViewList");
            codes = super.waybillStatusViewStr2Code(codeStr);
        } catch (Exception e) {
            log.warn("运单状态转换异常", e);
            return;
        }

        params.put("statusViewList", codes);
    }


    /**
     * 取消运单
     *
     * @param waybill
     * @param cargoOwnerLoginEcoUser
     */
    @RequestMapping(value = "cancelScatteredWaybill", method = RequestMethod.POST)
    public void cancelScatteredWaybillForApp(@RequestBody Waybill waybill, LoginEcoUser cargoOwnerLoginEcoUser) {
        scatteredWaybillService.cargoOwnerCancelBill(waybill, cargoOwnerLoginEcoUser);

        // 操作轨迹
        waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.CANCEL,
                OperateApplication.CARGO_OWNER_CLINET, buildTrackNotRequieParam(waybill, null),
                cargoOwnerLoginEcoUser);
    }


    /**
     * 新建落地配运单
     *
     * @param scatteredWaybillCreateVo
     * @param cargoOwnerLoginEcoUser
     * @return
     */
    @RequestMapping(value = "waybill/createWaybill", method = RequestMethod.POST)
    public Integer createScatteredWaybill(@RequestBody ScatteredWaybillCreateVo scatteredWaybillCreateVo, LoginEcoUser cargoOwnerLoginEcoUser) {
        Integer id = scatteredWaybillService.createScatteredWaybillForCargoOwner(scatteredWaybillCreateVo, cargoOwnerLoginEcoUser);

        // 操作轨迹--建单
        waybillOperateTrackService.insert(id, OperateType.CREATE_WAYBILL,
                OperateApplication.CARGO_OWNER_CLINET,
                buildTrackNotRequieParam(scatteredWaybillCreateVo.getWaybill(),
                        ("首次派车方式:" + Waybill.ReceiveWay.AUTO_ASSIGN)),
                cargoOwnerLoginEcoUser);

        return id;
    }

    /**
     * 用户添加身份证认证信息
     *
     * @param userVerifyVo
     * @param cargoOwnerLoginEcoUser
     */
    @RequestMapping(value = "addIdentifyInfo", method = RequestMethod.POST)
    public void addIdentifyInfo(@RequestBody UserVerifyVo userVerifyVo, LoginEcoUser cargoOwnerLoginEcoUser) {
        if (userVerifyVo == null) throw new BusinessException("paramNull", "errors.paramCanNotNullWithName", "参数");
        //姓名
        if (StringUtils.isBlank(userVerifyVo.getName()))
            throw new BusinessException("nameNull", "errors.paramCanNotNullWithName", "姓名");
        //身份证照
        if (StringUtils.isBlank(userVerifyVo.getIdentityCardNo()))
            throw new BusinessException("cardNONull", "errors.paramCanNotNullWithName", "身份证号");
        //手持身份证照
        if (StringUtils.isBlank(userVerifyVo.getHoldPhoto()))
            throw new BusinessException("cardNONull", "errors.paramCanNotNullWithName", "手持身份证照");

        userVerifyVo.setUserId(cargoOwnerLoginEcoUser.getUserId());
        //当前登录人，如果有认证信息则更新，没有则添加
        UserInfo userInfo = userService.findUserInfo(cargoOwnerLoginEcoUser.getUserId());

        UserVerification userVerification = userInfo.getUserVerification();
        //没有认证信息或认证不通过
        if (userVerification == null || NumberUtils.compare(userVerification.getIdcardStatus(), UserVerification.AuthenticationStatus.not.getValue()) == 0) {
            userVerificationService.createUserIdentityCardInfo(userVerifyVo, cargoOwnerLoginEcoUser);
        } else if (NumberUtils.compare(userVerification.getIdcardStatus(), UserVerification.AuthenticationStatus.wait.getValue()) == 0) {
            throw new BusinessException("statusError", "errors.common.prompt", "身份证信息正在审核中...");
        } else if (NumberUtils.compare(userVerification.getIdcardStatus(), UserVerification.AuthenticationStatus.pass.getValue()) == 0) {
            throw new BusinessException("statusError", "errors.common.prompt", "身份证信息已经审核通过.");
        }

        userInfo.setPassword(null);
        userInfo.setName(userVerifyVo.getName());

        userService.updateUser(userInfo, cargoOwnerLoginEcoUser);
    }

    /**
     * 身份证号校验
     *
     * @param cardNo
     * @return
     */
    @RequestMapping(value = "{cardNo}/checkCardNo", method = RequestMethod.GET)
    public boolean checkCardNo(@PathVariable("cardNo") String cardNo) {
        if (StringUtils.isBlank(cardNo)) return false;

        IDValidator validator = new IDValidator();
        return validator.isValid(cardNo);
    }

    /**
     * 落地配
     * 重新下单
     *
     * @param waybillId
     * @param cargoOwnerLoginEcoUser
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "scattered/waybill/{waybillId}/detail", method = RequestMethod.GET)
    public WaybillDetailInfo getScatteredBillDetail(@PathVariable("waybillId") Integer waybillId, LoginEcoUser cargoOwnerLoginEcoUser) {
        WaybillDetailInfo detailInfo = scatteredWaybillService.getScatteredBillDetail(waybillId, cargoOwnerLoginEcoUser);

        return detailInfo;
    }

    /**
     * 落地配-运单详情
     *
     * @param waybillId
     * @return
     */
    @ResponseBody
    @RequestMapping("scattered/waybill/{waybillId}/viewDetail")
    public ScatteredWaybillCargoOwnerDetailVo scatteredWaybillViewDetail(@PathVariable("waybillId") Integer waybillId, LoginEcoUser cargoOwnerLoginEcoUser) {
        ScatteredWaybillViewVo scatteredWaybillViewVo = scatteredWaybillService.getDetail(waybillId, cargoOwnerLoginEcoUser);

        ScatteredWaybillCargoOwnerDetailVo detailVo = new ScatteredWaybillCargoOwnerDetailVo();
        detailVo.setScatteredWaybillViewVo(scatteredWaybillViewVo);

        return detailVo;
    }

}
