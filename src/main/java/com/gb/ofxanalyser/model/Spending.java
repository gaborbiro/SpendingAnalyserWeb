package com.gb.ofxanalyser.model;

import java.util.Date;

import com.gb.ofxanalyser.util.ArrayUtils;

public class Spending {

	private Integer ID;
	private String description;
	private String date;
	private String amount;
	private String category;
	private boolean isSubscription;

	private Date properDate;

	public Spending(int ID, String description, Date properDate, String date, String amount, String category,
			boolean isSubscription) {
		this.ID = ID;
		this.description = description;
		this.properDate = properDate;
		this.date = date;
		this.amount = amount;
		this.category = category;
		this.isSubscription = isSubscription;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public boolean isSubscription() {
		return isSubscription;
	}

	public void setSubscription(boolean isSubscription) {
		this.isSubscription = isSubscription;
	}
}
