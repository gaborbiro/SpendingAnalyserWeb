package com.gb.ofxanalyser.service.finance;

import java.util.HashMap;
import java.util.Map;

import com.gb.ofxanalyser.model.Spending;

public class SpendingCategorizer {

	public static int PERDIOD_MONTH = 1;
	public static int PERDIOD_WEEK = 2;
	public static int PERDIOD_DAY = 3;
	public static int PERDIOD_YEAR = 4;
	public static int PERDIOD_ALL = 5;

	public Map<String, Double> getAverages(Spending[] spendings, int period) {
		Map<String, Double> map = new HashMap<String, Double>();

		for (Spending spending : spendings) {

		}
		return map;
	}
}
