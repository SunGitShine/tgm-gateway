package com.juma.tgm.gateway.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class JsonFieldFilterInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
		throws Exception {
		HandlerMethod method = (HandlerMethod)handler;
		JsonFieldCustomized ann = method.getMethodAnnotation(JsonFieldCustomized.class);
		if(ann != null){
			JsonFieldThreadLocal.set(ann.includes());
		}
		return true;
	}
	
	@Override
	public void postHandle(
			HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
		JsonFieldThreadLocal.unset();
	}
	
	@Override
	public void afterConcurrentHandlingStarted(
			HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
	}
	
}
