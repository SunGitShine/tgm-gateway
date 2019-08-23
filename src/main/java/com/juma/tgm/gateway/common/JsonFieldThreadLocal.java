package com.juma.tgm.gateway.common;

public class JsonFieldThreadLocal {

	private static final ThreadLocal<String[]> JSON_FIELD = new ThreadLocal<String[]>();
	
	public static void set(String[] values){
		JSON_FIELD.set(values);
	}
	
	public static void unset(){
		JSON_FIELD.remove();
	}
	
	public static String[] get(){
		return JSON_FIELD.get();
	}
	
}
