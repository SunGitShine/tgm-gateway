package com.juma.tgm.gateway.waybill.controller.util;

import com.giants.common.exception.BusinessException;
import com.juma.tgm.waybill.domain.WaybillBo;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * @ClassName: WaybillControllerUtil
 * @Description:
 * @author: liang
 * @date: 2018-03-28 15:35
 * @Copyright: 2018 www.jumapeisong.com Inc. All rights reserved.
 */
@Component
public class WaybillControllerUtil {

    /**
     * 预计完成时间不能早于计划用车时间
     *
     * @param waybillBo
     */
    public void planEstimateFinishTimeCheck(WaybillBo waybillBo) {
        try {
            if (DateUtils.truncatedCompareTo(waybillBo.getWaybill().getPlanDeliveryTime(),
                    waybillBo.getWaybill().getCmEstimateFinishTime(), Calendar.MINUTE) >= 0) {
                throw new BusinessException("estimateFinishTimeEarly", "waybill.error.estimate.finish.time.early");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            // ignore
        }
    }
}
