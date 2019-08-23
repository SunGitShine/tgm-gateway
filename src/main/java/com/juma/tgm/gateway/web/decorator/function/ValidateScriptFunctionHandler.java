/**
 * 
 */
package com.juma.tgm.gateway.web.decorator.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Form;
import org.apache.commons.validator.ValidatorAction;
import org.springframework.context.MessageSource;
import org.springmodules.validation.commons.MessageUtils;
import org.springmodules.validation.commons.ValidatorFactory;

import com.giants.common.SpringContextHelper;
import com.giants.common.collections.CollectionUtils;
import com.giants.decorator.core.Parameter;
import com.giants.decorator.core.TemplateEngine;
import com.giants.decorator.core.exception.TemplateException;
import com.giants.decorator.core.function.FunctionHandler;
import com.giants.decorator.html.HtmlTemplateEngine;

/**
 * @author vencent.lu
 *
 */
public class ValidateScriptFunctionHandler implements FunctionHandler {
	
	private static String[] dislodgeValidator = new String[]{"validwhen"};

	/* (non-Javadoc)
	 * @see com.giants.decorator.core.function.FunctionHandler#execute(java.util.Map, java.lang.Object, java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object execute(TemplateEngine templateEngine, Map<String, Object> globalVarMap, Object dataObj,
			List<Parameter> parameters) throws TemplateException {
		String formBean = (String) parameters.get(0).parse(globalVarMap,
				dataObj);
		String formName = (String) parameters.get(1).parse(globalVarMap, dataObj);
		String errorContainer = (String) parameters.get(2).parse(globalVarMap, dataObj);
		ValidatorFactory validatorFactory = SpringContextHelper
				.getSpringBean(ValidatorFactory.class);
		MessageSource messages = SpringContextHelper.getSpringBean(MessageSource.class);
		Form form = validatorFactory.getValidatorResources().getForm(
				Locale.getDefault(), formBean);
		if (form != null) {
			FormValidateConf formValidateConf;
			if (formName != null) {
				formValidateConf = new FormValidateConf(formName, errorContainer);
			} else {
				formValidateConf = new FormValidateConf(".form", errorContainer);
			}
			List<Field> fields = (List<Field>)form.getFields();
			for (Field field : fields) {
				FieldValidate fieldValidate = new FieldValidate(field.getProperty());
				List<ValidatorAction> ValidatorActions = this
						.createValidatorActions(validatorFactory,
								(List<String>) field.getDependencyList());
				for (ValidatorAction validatorAction : ValidatorActions) {
					if (!ArrayUtils.contains(dislodgeValidator,
							validatorAction.getName())) {
						String validateName = validatorAction.getName();
						if (validateName.equals("date")) {
							String datePattern = field
									.getVarValue("datePattern");
							if (datePattern.equals("yyyy-MM-dd")) {
								validateName = "dateISO";
							} else if (datePattern.equals("dd.MM.yyyy")) {
								validateName = "dateDE";
							}
						}
						Validate validate = new Validate(
								validateName,
								MessageUtils.getMessage(messages,
										Locale.getDefault(), validatorAction,
										field));
						if (validatorAction.getName().equals("doubleRange")
								|| validatorAction.getName().equals("range")
								|| validatorAction.getName().equals("intRange")
								|| validatorAction.getName().equals(
										"floatRange")) {
							validate.addArg("min", field.getVarValue("min"));
							validate.addArg("max", field.getVarValue("max"));
						} else if (validatorAction.getName().equals("mask")) {
							validate.addArg(
									"mask",
									new StringBuffer("/")
											.append(field
													.getVarValue(validatorAction
															.getName()))
											.append("/").toString());
						} else {
							String varValue = field.getVarValue(validatorAction
									.getName());
							if (varValue != null) {
								validate.addArg(validatorAction.getName(),
										varValue);
							}
						}
						fieldValidate.addValidate(validate);
					}
				}				
				formValidateConf.addFieldValidate(fieldValidate);
			}
			return ((HtmlTemplateEngine) templateEngine).loadWidgetTemplate(
					"validate-script").execute(globalVarMap, formValidateConf);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private List<ValidatorAction> createValidatorActions(
			ValidatorFactory validatorFactory, List<String> depends) {
		List<ValidatorAction> ValidatorActions = new ArrayList<ValidatorAction>();
		for (String depend : depends) {
			ValidatorAction validatorAction = validatorFactory
					.getValidatorResources().getValidatorAction(depend);
			List<String> actionDepends = (List<String>) validatorAction
					.getDependencyList();
			if (CollectionUtils.isNotEmpty(actionDepends)) {
				List<ValidatorAction> dependActions = this
						.createValidatorActions(validatorFactory, actionDepends);
				for (ValidatorAction dependAction : dependActions) {
					if (!ValidatorActions.contains(dependAction)) {
						ValidatorActions.add(dependAction);
					}
				}
			}
			ValidatorActions.add(validatorAction);
		}
		return ValidatorActions;
	}

}
