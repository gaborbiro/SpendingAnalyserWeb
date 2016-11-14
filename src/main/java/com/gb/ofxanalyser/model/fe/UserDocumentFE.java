package com.gb.ofxanalyser.model.fe;

public class UserDocumentFE {

	private Integer id;
	private String name;
	private String description;
	private String contentType;
	private String period;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	@Override
	public String toString() {
		return "UserDocumentFE [id=" + id + ", name=" + name + ", description=" + description + ", contentType="
				+ contentType + ", period=" + period + "]";
	}
}
