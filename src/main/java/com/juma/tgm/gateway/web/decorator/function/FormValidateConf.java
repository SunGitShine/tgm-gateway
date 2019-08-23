package com.juma.tgm.gateway.web.decorator.function;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vencent.lu
 *
 */
public class FormValidateConf {
	
	private String formName;
	private List<FieldValidate> fieldValidates;
	private String errorContainer;
	
	/**
	 * @param formName
	 */
	public FormValidateConf(String formName) {
		super();
		this.formName = formName;
	}
	
	/**
	 * @param formName
	 * @param errorContainer
	 */
	public FormValidateConf(String formName, String errorContainer) {
		super();
		this.formName = formName;
		this.errorContainer = errorContainer;
	}

	public void addFieldValidate(FieldValidate fieldValidate) {
		if (this.fieldValidates == null) {
			this.fieldValidates = new ArrayList<FieldValidate>();
		}
		this.fieldValidates.add(fieldValidate);
	}

	/**
	 * @return the formName
	 */
	public final String getFormName() {
		return formName;
	}
	
	/**
	 * @return the fieldValidates
	 */
	public final List<FieldValidate> getFieldValidates() {
		return fieldValidates;
	}

	/**
	 * @return the errorContainer
	 */
	public String getErrorContainer() {
		return errorContainer;
	}

}
