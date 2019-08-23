/**
 * 
 */
package com.juma.tgm.gateway.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.ObjectError;

import com.giants.common.collections.CollectionUtils;
import com.giants.common.exception.BusinessException;

/**
 * @author vencent.lu
 *
 */
public abstract class AbstractController extends BaseController {
	
	protected final Logger   logger = LoggerFactory.getLogger(this.getClass());
	
	public ObjectError conversionObjectError(BusinessException e) {
		Object[] arguments = null;
		if (CollectionUtils.isNotEmpty(e.getMessageArgs())) {
			arguments = e.getMessageArgs().toArray();
		}
		return new ObjectError(e.getErrorKey(),
				new String[] { e.getErrorMessageKey() }, arguments,
				e.getErrorMessageKey());
	}

}
