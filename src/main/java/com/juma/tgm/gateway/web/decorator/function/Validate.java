/**
 * 
 */
package com.juma.tgm.gateway.web.decorator.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * @author vencent.lu
 *
 */
public class Validate {
	
	private String name;
	private List<Map<String,String>> args;
	private String errorMsg;
		
	/**
	 * @param name
	 * @param value
	 * @param errorMsg
	 */
	public Validate(String name,String errorMsg) {
		super();
		this.name = name;
		this.errorMsg = errorMsg;
	}
	
	public void addArg(String key, String argValue) {
		if (this.args == null) {
			this.args = new ArrayList<Map<String,String>>();
		}
		if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(argValue)) {
			Map<String,String> argMap = new HashMap<String,String>();
			argMap.put("key", key);
			argMap.put("value", argValue);
			this.args.add(argMap);
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
		
	/**
	 * @return the args
	 */
	public final List<Map<String, String>> getArgs() {
		return args;
	}

	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

}
