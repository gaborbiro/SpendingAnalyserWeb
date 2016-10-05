package com.gb.ofxanalyser.model;

import java.util.Date;

public class Spending {

	private Integer ID;
	private String description;
	private String date;
	private String amount;

	private Date properDate;

	public Spending(int ID, String description, Date properDate, String date, String amount) {
		this.ID = ID;
		this.description = description;
		this.properDate = properDate;
		this.date = date;
		this.amount = amount;
	}

	public Integer getID() {
		return ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getProperDate() {
		return properDate;
	}

	public void setProperDate(Date properDate) {
		this.properDate = properDate;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
}
