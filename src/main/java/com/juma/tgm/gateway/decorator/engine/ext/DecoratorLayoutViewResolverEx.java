/**
* @Title: DecoratorLayoutViewResolverEx.java
* @Package com.juma.cms.gateway.spring.view
*<B>Copyright</B> Copyright (c) 2016 www.jumapeisong.com All rights reserved. <br />
* 本软件源代码版权归驹马,未经许可不得任意复制与传播.<br />
* <B>Company</B> 驹马配送
* @date 2016年8月11日 上午10:18:53
* @version V1.0  
 */
package com.juma.tgm.gateway.decorator.engine.ext;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import com.giants.decorator.core.TemplateEngine;

/**
 *@Description: 
 *@author Administrator
 *@date 2016年8月11日 上午10:18:53
 *@version V1.0  
 */
public class DecoratorLayoutViewResolverEx extends AbstractTemplateViewResolver {

	private DecoratorLayoutEx decoratorLayout;
	
	private Map<String,String> domainMap = new HashMap<String,String>();
	
	public DecoratorLayoutViewResolverEx() {
		setViewClass(requiredViewClass());
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.view.AbstractTemplateViewResolver#requiredViewClass()
	 */
	@Override
	protected Class<?> requiredViewClass() {
		return DecoratorLayoutViewEx.class;
	}
	
	@Override
	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
	    DecoratorLayoutViewEx decoratorLayoutView = (DecoratorLayoutViewEx)super.buildView(viewName);
        decoratorLayoutView.setDecoratorLayout(this.decoratorLayout);
        return decoratorLayoutView;
	}

	/**
	 * @param templateEngine the templateEngine to set
	 */
	public void setTemplateEngine(TemplateEngine templateEngine) {
        this.decoratorLayout = new DecoratorLayoutEx(templateEngine);
    }

	public Map<String, String> getDomainMap() {
		return domainMap;
	}

	public void setDomainMap(Map<String, String> domainMap) {
		this.domainMap = domainMap;
		decoratorLayout.setDomainMap(domainMap);
	}
	
}
