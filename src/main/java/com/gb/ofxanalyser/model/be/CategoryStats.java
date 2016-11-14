package com.gb.ofxanalyser.model.be;

public class CategoryStats {
	public String category;
	public String monthlyAvg;
	public String[] months;

	public CategoryStats(String category, String monthlyAvg, String[] months) {
		this.category = category;
		this.monthlyAvg = monthlyAvg;
		this.months = months;
	}

	public String getCategory() {
		return category;
	}

	public String getMonthlyAvg() {
		return monthlyAvg;
	}

	public String[] getMonths() {
		return months;
	}
}
