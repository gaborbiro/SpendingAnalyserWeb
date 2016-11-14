package com.gb.ofxanalyser.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.gb.ofxanalyser.model.fe.UserFE;

/**
 * Note: empty fields are automatically validated. This class is for fancier
 * validations
 */
@Component
public class UserValidator implements Validator {

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	@Override
	public boolean supports(Class<?> clazz) {
		return UserFE.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		UserFE user = (UserFE) target;

		if (!user.getEmail().matches(EMAIL_PATTERN)) {
			errors.rejectValue("email", "invalid.email");
		}
	}
}
