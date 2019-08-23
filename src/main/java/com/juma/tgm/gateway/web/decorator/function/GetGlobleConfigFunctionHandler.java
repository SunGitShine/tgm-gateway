/**
* @Title: GetWalletHttpFunctionHandler.java
* @Package com.juma.tgm.gateway.web.decorator.function
*<B>Copyright</B> Copyright (c) 2016 www.jumapeisong.com All rights reserved. <br />
* 本软件源代码版权归驹马,未经许可不得任意复制与传播.<br />
* <B>Company</B> 驹马配送
* @date 2016年5月27日 上午10:44:40
* @version V1.0  
 */
package com.juma.tgm.gateway.web.decorator.function;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.support.PropertiesLoaderSupport;

import com.giants.common.SpringContextHelper;
import com.giants.decorator.core.Parameter;
import com.giants.decorator.core.TemplateEngine;
import com.giants.decorator.core.exception.TemplateException;
import com.giants.decorator.core.function.FunctionHandler;

/**
 *@Description: 钱包地址
 *@author Administrator
 *@date 2016年5月27日 上午10:44:40
 *@version V1.0  
 */
public class GetGlobleConfigFunctionHandler implements FunctionHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(GetGlobleConfigFunctionHandler.class);

	private Properties props;
	
	public GetGlobleConfigFunctionHandler(){
		try {
			Method mergeProperties = PropertiesLoaderSupport.class.getDeclaredMethod("mergeProperties");
			mergeProperties.setAccessible(true);
			props=(Properties) mergeProperties.invoke(SpringContextHelper.getSpringBean(PropertyPlaceholderConfigurer.class));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	@Override
	public Object execute(TemplateEngine templateEngine, Map<String, Object> globalVarMap, Object dataObj, List<Parameter> parameters) throws TemplateException {
		String paramKey = (String) parameters.get(0).parse(globalVarMap,dataObj);
		return props.get(paramKey);
	}

}
