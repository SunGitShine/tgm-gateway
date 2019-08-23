/**
 * 
 */
package com.juma.tgm.gateway.web.decorator.function;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vencent.lu
 *
 */
public class FieldValidate {
	
	private String fieldName;
	private List<Validate> validates;
	
	/**
	 * @param fieldName
	 */
	public FieldValidate(String fieldName) {
		super();
		this.fieldName = fieldName;
	}
	
	public void addValidate(Validate validate) {
		if (this.validates == null) {
			this.validates = new ArrayList<Validate>();
		}
		this.validates.add(validate);
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}
	
	/**
	 * @return the validates
	 */
	public List<Validate> getValidates() {
		return validates;
	}

}
