package com.gb.ofxanalyser.service.categories;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * This class can tell which category a certain transaction falls into.<br>
 */
@Service(value = "categorisationService")
public class CategorisationService {

	public static final String CATEGORY_SUBSCRIPTION = "Subscription";
	public static final String CATEGORY_UNKNOWN = "Unknown";

	private Map<String, List<String>> map;

	public String getCategoryForTransaction(String description) {
		if (map == null) {
			map = CategoriesParser.getMapping();
		}
		String category = null;

		for (String key : map.keySet()) {
			if (description.toLowerCase().contains(key.toLowerCase())) {
				category = getFirstNonSpecialCategory(map.get(key));
			}
		}
		if (category == null) {
			category = CATEGORY_UNKNOWN;
		}
		return category;
	}

	public boolean isSubscription(String description) {
		if (map == null) {
			map = CategoriesParser.getMapping();
		}
		for (String key : map.keySet()) {
			if (description.toLowerCase().contains(key.toLowerCase())) {
				return map.get(key).contains(CATEGORY_SUBSCRIPTION);
			}
		}
		return false;
	}

	private String getFirstNonSpecialCategory(List<String> categories) {
		for (String category : categories) {
			if (!isSpecialCategory(category)) {
				return category;
			}
		}
		return null;
	}

	private boolean isSpecialCategory(String category) {
		switch (category) {
		case CATEGORY_SUBSCRIPTION:
			return true;
		default:
			return false;
		}
	}
}
