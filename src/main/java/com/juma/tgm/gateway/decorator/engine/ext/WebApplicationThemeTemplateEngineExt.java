package com.juma.tgm.gateway.decorator.engine.ext;

import org.springframework.beans.factory.InitializingBean;

import com.giants.decorator.html.Theme;
import com.giants.decorator.springframework.engine.WebApplicationThemeTemplateEngine;

public class WebApplicationThemeTemplateEngineExt extends WebApplicationThemeTemplateEngine implements InitializingBean {

	private String name;
	
	private String path;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Theme theme = new ThemeExt(name, path);
		this.selectTheme(theme);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
