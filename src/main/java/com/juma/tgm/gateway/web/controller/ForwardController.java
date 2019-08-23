/**
 * 
 */
package com.juma.tgm.gateway.web.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author vencent.lu
 *
 * Create Date:2014年2月24日
 */
@Controller
public class ForwardController {
	
	@RequestMapping(value="forward/**")
	public ModelAndView forward(HttpServletRequest request) {
		String servletPath = request.getServletPath().intern();
		return new ModelAndView(servletPath.replace("/forward/", "").replaceAll(
				"\\.[^\\.]+$", ""));

	}
	   
    @RequestMapping(value="cdate")
    @ResponseBody
    public Date cdate(HttpServletRequest request) {
        return new Date();

    }
}
