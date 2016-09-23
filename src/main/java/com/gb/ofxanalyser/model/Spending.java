package com.gb.ofxanalyser.model;

public class Spending {

	private String description;
	private String date;
	private String amount;

	public Spending(String description, String date, String amount) {
		this.description = description;
		this.date = date;
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
