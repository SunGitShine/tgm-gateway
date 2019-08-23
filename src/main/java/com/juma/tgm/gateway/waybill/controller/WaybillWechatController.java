package com.juma.tgm.gateway.waybill.controller;

import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.user.domain.LoginEcoUser;
import com.juma.tgm.cityManage.domain.CityManage;
import com.juma.tgm.cityManage.service.CityManageService;
import com.juma.tgm.common.Constants;
import com.juma.tgm.customer.domain.CargoOwnerLoginUser;
import com.juma.tgm.gateway.common.BaseController;
import com.juma.tgm.truck.domain.TruckType;
import com.juma.tgm.truck.domain.TruckTypeFreightBo;
import com.juma.tgm.truck.service.TruckTypeFreightService;
import com.juma.tgm.version.service.VersionService;
import com.juma.tgm.waybill.domain.DistanceAndPriceData;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillBo;
import com.juma.tgm.waybill.domain.WaybillDetailInfo;
import com.juma.tgm.waybill.domain.vo.DistanceAndPriceParamVo;
import com.juma.tgm.waybill.service.WaybillService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Deprecated
@Controller
@RequestMapping(value = "waybill/wechat")
public class WaybillWechatController extends BaseController {

    @Resource
    private WaybillService waybillService;
    @Resource
    private TruckTypeFreightService truckTypeFreightService;
    @Resource
    private CityManageService cityManageService;
    @Resource
    private VersionService versionService;

    /**
     * 分页查询运单列表(货主端)
     */
    @RequestMapping(value = "search", method = RequestMethod.POST)
    @ResponseBody
    public Page<WaybillDetailInfo> search(@RequestBody PageCondition pageCondition, CargoOwnerLoginUser cargoOwnerLoginUser, LoginEcoUser cargoOwnerLoginEcoUser) {
        pageCondition.getFilters().put("truckCustomerId", cargoOwnerLoginUser.getTruckCustomerId());
        return waybillService.searchWechatPageList(pageCondition, cargoOwnerLoginEcoUser);
    }

    /**
     * 不建议使用：运单详情(货主端：驹马生态用户使用)---此方法只为微信端运单地图详情提供服务，为临时接口
     */
    @Deprecated
    @RequestMapping(value = "truckCustomer/{waybillId}", method = RequestMethod.GET)
    @ResponseBody
    public Waybill findDetailsForTruckCustomer(@PathVariable Integer waybillId, LoginEcoUser cargoOwnerLoginEcoUser) {
        if (waybillId == null) {
            throw new BusinessException("validationFailure", "errors.validation.failure");
        }
        return waybillService.findWaybillBo(waybillId, cargoOwnerLoginEcoUser);
    }

    /**
     * 得到距离和价格信息
     */
    @RequestMapping(value = "getDistanceAndPrice", method = RequestMethod.POST)
    @ResponseBody
    public DistanceAndPriceData getDistanceAndPrice(@RequestBody DistanceAndPriceParamVo dp, LoginEcoUser cargoOwnerLoginEcoUser) {
        WaybillBo bo = new WaybillBo();
        bo.setTruckRequire(dp.getTruckRequire());
        bo.setWaybillParam(dp.getWaybillParam());
        bo.setWaybill(dp.getWaybill());
        return waybillService.calWaybillPrice(null, null, bo, cargoOwnerLoginEcoUser);
    }

    /**
     * 创建运单
     */
    @RequestMapping(value = "createWaybill", method = RequestMethod.POST)
    @ResponseBody
    public Integer createWaybill(@RequestBody WaybillBo waybillBo, LoginEcoUser cargoOwnerLoginEcoUser) {
        throw new BusinessException("errors.common.prompt", "errors.common.prompt","已停用，不能下单");
    }


    /**
     * 微信货主端1.10版建单接口
     *
     * @param waybillBo
     * @param cargoOwnerLoginEcoUser
     * @return
     */
    @RequestMapping(value = "createWaybillForWx", method = RequestMethod.POST)
    @ResponseBody
    public Integer createWaybillNew(@RequestBody WaybillBo waybillBo, CargoOwnerLoginUser cargoOwnerLoginUser, LoginEcoUser cargoOwnerLoginEcoUser) {
        throw new BusinessException("errors.common.prompt", "errors.common.prompt","已停用，不能下单");
    }

    /**
     * 运费规则
     */
    @RequestMapping(value = "truckTypeFreight/info", method = RequestMethod.GET)
    @ResponseBody
    public List<TruckTypeFreightBo> createWaybill(LoginEcoUser cargoOwnerLoginEcoUser) {
        List<TruckTypeFreightBo> result = new ArrayList<TruckTypeFreightBo>();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNo(1);
        pageCondition.setPageSize(10);
        pageCondition.getFilters().put("isDelete", false);
        String regionCode = versionService.findDefaultRegionCode(Constants.DEFAULT_FRIGHT_CODE_KEY);
        CityManage cityManage = cityManageService.findCityManageByCityCode(CityManage.Sign.AREA_MANAGE.getCode(),
                regionCode);
        if (null == cityManage) {
            return result;
        }

        pageCondition.getFilters().put("cityManageId", cityManage.getParentCityManageId());
        Page<TruckTypeFreightBo> page = truckTypeFreightService.search(pageCondition, cargoOwnerLoginEcoUser);
        if (null == page) {
            return result;
        }

        List<TruckTypeFreightBo> list = (List<TruckTypeFreightBo>) page.getResults();
        for (TruckTypeFreightBo bo : list) {
            TruckType truckType = bo.getTruckType();
            if ((truckType.getVehicleBoxType() == 34 || truckType.getVehicleBoxType() == 31)
                    && truckType.getTruckLengthId() == 3) {
                bo.setTruckType(null);
                result.add(bo);
            }
        }
        return result;
    }
}
