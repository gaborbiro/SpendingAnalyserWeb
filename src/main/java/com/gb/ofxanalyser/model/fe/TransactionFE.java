package com.gb.ofxanalyser.model.fe;

public class TransactionFE {

	private String description;
	private String date;
	private String amount;
	private String category;
	private boolean isSubscription;

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

	@Override
	public String toString() {
		return "TransactionFE [description=" + description + ", date=" + date + ", amount=" + amount + ", category="
				+ category + ", isSubscription=" + isSubscription + "]";
	}
}
