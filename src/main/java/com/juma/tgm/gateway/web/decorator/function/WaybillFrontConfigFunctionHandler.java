/**
* @Title: WaybillFrontConfigFunctionHandler.java
* @Package com.juma.tgm.gateway.web.decorator.function
*<B>Copyright</B> Copyright (c) 2016 www.jumapeisong.com All rights reserved. <br />
* 本软件源代码版权归驹马,未经许可不得任意复制与传播.<br />
* <B>Company</B> 驹马配送
* @date 2016年9月5日 下午3:24:56
* @version V1.0  
 */
package com.juma.tgm.gateway.web.decorator.function;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.giants.common.SpringContextHelper;
import com.giants.decorator.core.Parameter;
import com.giants.decorator.core.TemplateEngine;
import com.giants.decorator.core.exception.TemplateException;
import com.giants.decorator.core.function.FunctionHandler;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.bo.WaybillFrontConfig;
import com.juma.tgm.waybill.service.WaybillService;

/**
 *@Description: 
 *@author Administrator
 *@date 2016年9月5日 下午3:24:56
 *@version V1.0  
 */
public class WaybillFrontConfigFunctionHandler implements FunctionHandler {

	@Override
	public Object execute(TemplateEngine templateEngine, Map<String, Object> globalVarMap, Object dataObj,
			List<Parameter> parameters) throws TemplateException {
		WaybillService waybillService = SpringContextHelper.getSpringBean(WaybillService.class);
		Integer waybillId = (Integer) parameters.get(0).parse(globalVarMap,dataObj);
		Waybill waybill = waybillService.getWaybill(waybillId);
		WaybillFrontConfig frontConfig = new WaybillFrontConfig();
		if(waybill != null){
			frontConfig.setPlanDeliveryTime(waybill.getPlanDeliveryTime());
			frontConfig.setFinishDeliveryTime(new Date(waybill.getPlanDeliveryTime().getTime()+waybill.getEstimateTimeConsumption()*60*1000));
		}
		return frontConfig;
	}

}
