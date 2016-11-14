package com.gb.ofxanalyser.model.fe;

import org.springframework.web.multipart.MultipartFile;

public class FileBucket {

	MultipartFile[] files;

	String description;

	public MultipartFile[] getFiles() {
		return files;
	}

	public void setFiles(MultipartFile[] files) {
		this.files = files;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}