package com.gb.ofxanalyser.model;

import java.util.Date;

public class Spending implements Comparable<Spending> {

	private Integer ID;
	private String description;
	private String date;
	private String amount;
	private String category;
	private boolean isSubscription;

	private Double actualAmount;
	private Date actualDate;

	public Spending(int ID, String description, Date actualDate, String date, Double actualAmount, String amount,
			String category, boolean isSubscription) {
		this.ID = ID;
		this.description = description;
		this.actualDate = actualDate;
		this.date = date;
		this.actualAmount = actualAmount;
		this.amount = amount;
		this.category = category;
		this.isSubscription = isSubscription;
	}

	public Integer getID() {
		return ID;
	}

	public String getDescription() {
		return description;
	}

	public Date getActualDate() {
		return actualDate;
	}

	public String getDate() {
		return date;
	}

	public Double getActualAmount() {
		return actualAmount;
	}

	public String getAmount() {
		return amount;
	}

	public String getCategory() {
		return category;
	}

	public boolean isSubscription() {
		return isSubscription;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actualAmount == null) ? 0 : actualAmount.hashCode());
		result = prime * result + ((actualDate == null) ? 0 : actualDate.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Spending other = (Spending) obj;
		if (actualAmount == null) {
			if (other.actualAmount != null)
				return false;
		} else if (!actualAmount.equals(other.actualAmount))
			return false;
		if (actualDate == null) {
			if (other.actualDate != null)
				return false;
		} else if (!actualDate.equals(other.actualDate))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Spending [ID=" + ID + ", description=" + description + ", date=" + date + ", amount=" + amount
				+ ", category=" + category + ", isSubscription=" + isSubscription + ", actualDate=" + actualDate + "]";
	}

	@Override
	public int compareTo(Spending o) {
		return o.hashCode() - hashCode();
	}
}
