package com.juma.tgm.gateway.customer.controller;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.user.domain.LoginEcoUser;
import com.juma.conf.domain.Region;
import com.juma.tgm.cityManage.domain.CityManage;
import com.juma.tgm.cityManage.domain.CityManageInfo;
import com.juma.tgm.cityManage.service.CityManageService;
import com.juma.tgm.common.Constants;
import com.juma.tgm.customer.domain.CargoOwnerLoginUser;
import com.juma.tgm.customer.domain.vo.BindVo;
import com.juma.tgm.gateway.common.BaseController;
import com.juma.tgm.gateway.customer.WechatBusinessModule.WechatBusinessBuilder;
import com.juma.tgm.gateway.customer.bo.WechatDeliveryAddressBo;
import com.juma.tgm.gateway.customer.bo.WechatWaybillTrackBo;
import com.juma.tgm.gateway.customer.vo.TruckCustomerInfoVo;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.service.WaybillService;
import com.juma.tgm.weixin.service.WeixinService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Libin.Wei
 * @version 1.0.0
 * @ClassName WechatTruckCustomerController.java
 * @Description 微信货主端
 * @Date 2017年1月4日 上午11:11:30
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("truckCustomer/wechat")
public class WechatTruckCustomerController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(WechatTruckCustomerController.class);

    @Resource
    private WeixinService weixinService;
    @Resource
    private WaybillService waybillService;
    @Resource
    private WechatBusinessBuilder wechatBusinessBuilder;
    @Resource
    private CityManageService cityManageService;

    /**
     * 货主端 ：通过微信code获取openId- 微信获取code 回调地址
     */
    @RequestMapping(value = "openId", method = RequestMethod.GET)
    public String weixinAuth(String code, String toUrl) {
        String redirectUrl = null;
        String openId = weixinService.getCustomerOpenId(code);
        if (StringUtils.isBlank(openId)) {
            return "redirect:" + Constants.WECHAT_ERROR_PAGE_URL;
        }
        if (StringUtils.isNotBlank(toUrl)) {
            if (toUrl.contains("?")) {
                redirectUrl = toUrl + "&openId=" + openId;
            } else {
                redirectUrl = toUrl + "?openId=" + openId;
            }
        }
        return "redirect:" + redirectUrl;
    }

    /**
     * 获取用户信息:下期删除3.1.0
     */
    @Deprecated
    @ResponseBody
    @RequestMapping(value = "mobileNumber", method = RequestMethod.GET)
    public String mobileNumber(LoginEcoUser cargoOwnerLoginEcoUser) {
        return null;
    }

    /**
     * 根据手机后获取地区编码
     */
    @ResponseBody
    @RequestMapping(value = "{mobileNumber}/mobileNumber", method = RequestMethod.GET)
    public String mobileNumber(@PathVariable String mobileNumber, LoginEcoUser cargoOwnerLoginEcoUser) {
        return null;
    }

    /**
     * 获取用户信息
     */
    @ResponseBody
    @RequestMapping(value = "info", method = RequestMethod.GET)
    public TruckCustomerInfoVo tuckCustomerInfo(LoginEcoUser cargoOwnerLoginEcoUser) {
        TruckCustomerInfoVo info = new TruckCustomerInfoVo();
        logger.info("ecoUserId:" + cargoOwnerLoginEcoUser.getEcoUserId());
        return info;
    }

    /**
     * 当前登录人绑定微信
     */
    @ResponseBody
    @RequestMapping(value = "bindUser", method = RequestMethod.POST)
    public void bindUser(@RequestBody BindVo bindVo, LoginEcoUser cargoOwnerLoginEcoUser) {
    }

    /**
     * 获取运单轨迹列表
     *
     * @param pageCondition
     * @param cargoOwnerLoginEcoUser
     * @return
     */
    @RequestMapping(value = "waybillTrackInfo", method = RequestMethod.POST)
    @ResponseBody
    public Page<WechatWaybillTrackBo> waybillTrackInfo(@RequestBody PageCondition pageCondition,
            CargoOwnerLoginUser cargoOwnerLoginUser, LoginEcoUser cargoOwnerLoginEcoUser) {
        // 派车中、待配送、配送中的运单列表
        this.buildQueryParam(pageCondition, cargoOwnerLoginUser);

        Page<WechatWaybillTrackBo> finalData = new Page<>(pageCondition.getPageNo(), pageCondition.getPageSize(), 0,
                null);
        // 运单列表
        Page<Waybill> datas = waybillService.searchWaybillBasicInfo(pageCondition);

        Collection<Waybill> dataList = datas.getResults();
        if (CollectionUtils.isEmpty(dataList)) {
            return finalData;
        }

        List<WechatWaybillTrackBo> finalList = wechatBusinessBuilder.buildTrackDetailBo(dataList,
                WechatBusinessBuilder.allOperationList);
        finalData.setTotal(datas.getTotal());
        finalData.setResults(finalList);

        return finalData;

    }

    /**
     * 派车中配送地信息
     *
     * @param cargoOwnerLoginEcoUser
     * @return
     */
    @RequestMapping("{waybillId}/deliverAddress")
    @ResponseBody
    public WechatDeliveryAddressBo getWechatDeliveryAddressBo(@PathVariable("waybillId") Integer waybillId,
            LoginEcoUser cargoOwnerLoginEcoUser) {
        Waybill waybill = waybillService.getWaybill(waybillId);
        if (waybill == null)
            return null;

        return wechatBusinessBuilder.buildDeliveryAddressBo(waybill);
    }

    /**
     * 运单概览参数处理
     *
     * @param pageCondition
     * @param cargoOwnerLoginUser
     */
    private void buildQueryParam(PageCondition pageCondition, CargoOwnerLoginUser cargoOwnerLoginUser) {
        Map<String, Object> params = pageCondition.getFilters();

        if (params == null) {
            params = new HashMap<>();
            pageCondition.setFilters(params);
        }
        // 用车人
        params.put("truckCustomerId", cargoOwnerLoginUser.getTruckCustomerId());

        // 状态code转换
        List<Integer> codes = null;
        try {
            List<String> codeStr = (List<String>) params.get("statusViewList");
            codes = this.waybillStatusViewStr2Code(codeStr);
        } catch (Exception e) {
            logger.error("运单状态转换异常", e);
            return;
        }

        params.put("statusViewList", codes);

    }

    /**
     * 已开通城市
     */
    @RequestMapping(value = "already/opened/city/{regionCode}", method = RequestMethod.GET)
    @ResponseBody
    public List<Region> alreadyOpenedCity(@PathVariable("regionCode") String regionCode) {
        List<Region> result = new ArrayList<Region>();
        CityManageInfo cityManageInfo = null;
        if (StringUtils.isBlank(regionCode)) {
            cityManageInfo = cityManageService.getProvinceList(null, null);
        } else {
            CityManage example = new CityManage();
            example.setProvinceCode(regionCode);
            cityManageInfo = cityManageService.getCityList(example);
        }

        if (null == cityManageInfo) {
            return result;
        }

        // 去重使用
        List<String> hasRegionCode = new ArrayList<String>();

        for (CityManage cityManage : cityManageInfo.getCityManageList()) {
            String curRegionCode = StringUtils.isBlank(regionCode) ? cityManage.getProvinceCode()
                    : cityManage.getCityCode();
            if (hasRegionCode.contains(curRegionCode)) {
                continue;
            }
            hasRegionCode.add(curRegionCode);

            Region region = new Region();
            region.setRegionCode(
                    StringUtils.isBlank(regionCode) ? cityManage.getProvinceCode() : cityManage.getCityCode());
            region.setRegionName(
                    StringUtils.isBlank(regionCode) ? cityManage.getProvinceName() : cityManage.getCityName());
            result.add(region);
        }
        return result;
    }
}
