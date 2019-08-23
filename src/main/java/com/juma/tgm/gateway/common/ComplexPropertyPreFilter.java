package com.juma.tgm.gateway.common;

import java.util.HashSet;
import java.util.Set;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;

public class ComplexPropertyPreFilter implements PropertyPreFilter {

	private final Set<String> includes = new HashSet<String>();
	
    private final Set<String> excludes = new HashSet<String>();
	
	@Override
	public boolean apply(JSONSerializer serializer, Object object, String name) {
		if(includes.contains(name)){
			return true;
		}
		return false;
	}

	public Set<String> getIncludes() {
		return includes;
	}

	public Set<String> getExcludes() {
		return excludes;
	}

}
