/**
* @Title: MessageTestController.java
* @Package com.juma.tgm.gateway.message.controller
*<B>Copyright</B> Copyright (c) 2016 www.jumapeisong.com All rights reserved. <br />
* 本软件源代码版权归驹马,未经许可不得任意复制与传播.<br />
* <B>Company</B> 驹马配送
* @date 2016年6月23日 下午4:30:56
* @version V1.0  
 */
package com.juma.tgm.gateway.message.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.juma.message.gateway.service.MessageServiceProvider;
/**
 *@Description: 消息测试
 *@author zxh
 *@date 2016年6月23日 下午4:30:56
 *@version V1.0  
 */
@Controller
@RequestMapping(value = "message")
public class MessageTestController {

	@Resource
	private MessageServiceProvider messageServiceProvider;
	
	
}
