/**
* @Title: DecoratorLayoutViewEx.java
* @Package com.juma.cms.gateway.spring.view
*<B>Copyright</B> Copyright (c) 2016 www.jumapeisong.com All rights reserved. <br />
* 本软件源代码版权归驹马,未经许可不得任意复制与传播.<br />
* <B>Company</B> 驹马配送
* @date 2016年8月11日 上午10:22:51
* @version V1.0  
 */
package com.juma.tgm.gateway.decorator.engine.ext;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.giants.analyse.profiler.ExecutionTimeProfiler;
import com.giants.decorator.html.ThemeTemplateEngine;
import com.giants.decorator.springframework.mvc.DecoratorView;

/**
 *@Description: 
 *@author Administrator
 *@date 2016年8月11日 上午10:22:51
 *@version V1.0  
 */
public class DecoratorLayoutViewEx extends DecoratorView {
	
    private final Logger log = LoggerFactory.getLogger(DecoratorLayoutViewEx.class);
    
	private DecoratorLayoutEx decoratorLayout;

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.view.AbstractTemplateView#renderMergedTemplateModel(java.util.Map, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void renderMergedTemplateModel(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ExecutionTimeProfiler.enter("process decorator layout");
        
		ThemeTemplateEngine themeTemplateEngine = (ThemeTemplateEngine)decoratorLayout.getTemplateEngine();
		ThemeExt themeExt = new ThemeExt();
        themeExt.setName("smartTruck");
        
        String host = request.getHeader("host");
        
        String path = decoratorLayout.getDomainMap().get(host);
        themeExt.setPath(path);
        themeTemplateEngine.selectTheme(themeExt);
        
        log.debug("跳转域名：{}", host);
        log.debug("跳转文件夹：{}", path);
        
		response.getWriter().println(
				this.decoratorLayout.renderView(this.getUrl(), request,
						this.createGlobalVarMap(model, request), model));
		ExecutionTimeProfiler.release();
	}

	/**
	 * @param decoratorLayout the decoratorLayout to set
	 */
	public void setDecoratorLayout(DecoratorLayoutEx decoratorLayout) {
		this.decoratorLayout = decoratorLayout;
	}
}
