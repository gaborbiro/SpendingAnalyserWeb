package com.gb.ofxanalyser.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.gb.ofxanalyser.model.FileBucket;

@Component
public class FileValidator implements Validator {

	public boolean supports(Class<?> clazz) {
		return FileBucket.class.isAssignableFrom(clazz);
	}

	public void validate(Object obj, Errors errors) {
		FileBucket file = (FileBucket) obj;

		if (file.getFiles() != null) {
			if (file.getFiles().length == 0 || file.getFiles()[0].getSize() == 0) {
				errors.rejectValue("files", "missing.file");
			}
		}
	}
}
