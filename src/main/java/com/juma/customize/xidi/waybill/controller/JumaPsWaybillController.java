package com.juma.customize.xidi.waybill.controller;

import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.customize.annotation.CustomizeLayer;
import com.juma.customize.annotation.Customized;
import com.juma.tgm.common.Constants;
import com.juma.tgm.crm.service.CustomerInfoService;
import com.juma.tgm.gateway.waybill.controller.util.WaybillControllerUtil;
import com.juma.tgm.truck.service.TruckTypeFreightService;
import com.juma.tgm.waybill.domain.DistanceAndPriceData;
import com.juma.tgm.waybill.domain.TaxRate;
import com.juma.tgm.waybill.domain.WaybillBo;
import com.juma.tgm.waybill.domain.vo.DistanceAndPriceParamVo;
import com.juma.tgm.waybill.service.TaxRateService;
import com.juma.tgm.waybill.service.WaybillService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName: JumaPsWaybillController
 * @Description:
 * @author: liang
 * @date: 2018-03-28 15:31
 * @Copyright: 2018 www.jumapeisong.com Inc. All rights reserved.
 */
@Customized(tenantKey = Constants.TENANT_KEY_JUMA_LOGISTICS, layer = CustomizeLayer.controller)
@Component
public class JumaPsWaybillController {


    @Resource
    private TruckTypeFreightService truckTypeFreightService;

    @Resource
    private CustomerInfoService customerInfoService;

    @Resource
    private WaybillService waybillService;

    @Resource
    private TaxRateService taxRateService;

    @Resource
    private WaybillControllerUtil waybillControllerUtil;

    /**
     * 驹马专车报价接口
     *
     * @param dp
     * @param loginEmployee
     * @return
     */
    public DistanceAndPriceData getDistanceAndPrice(DistanceAndPriceParamVo dp, LoginEmployee loginEmployee) {
        WaybillBo bo = new WaybillBo();
        bo.setTruckRequire(dp.getTruckRequire());
        bo.setWaybill(dp.getWaybill());
        waybillControllerUtil.planEstimateFinishTimeCheck(bo);

        // TODO 4.6.1之后删除
        if (null != dp.getTruckRequire() && null != dp.getTruckRequire().getTaxRateId()) {
            TaxRate taxRate = taxRateService.getTaxRate(dp.getTruckRequire().getTaxRateId());
            dp.getTruckRequire().setTaxRateValue(taxRate == null ? null : taxRate.getTaxRateValue());
        }

        //计算并组装专车价格
        DistanceAndPriceData priceData = waybillService.doCalculateJumaLogisticsPrice(dp, loginEmployee);

        return priceData;
    }

}
