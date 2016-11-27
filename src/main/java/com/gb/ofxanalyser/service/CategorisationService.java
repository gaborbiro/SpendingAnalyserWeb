package com.gb.ofxanalyser.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gb.ofxanalyser.preferences.AppPreferences;
import com.gb.ofxanalyser.util.MapFileReader;

/**
 * This class can tell which category a certain transaction falls into.<br>
 */
@Service(value = "categorisationService")
public class CategorisationService {

	public static final String CATEGORY_SUBSCRIPTION = "Subscription";
	private static final String MAPPING_FILE_NAME = "categories.map";

	@Autowired
	AppPreferences prefs;

	private Map<String, List<String>> cache;

	public boolean hasMappingFileChanged() {
		return getFile().lastModified() > prefs.getMappingFileLastAccess();
	}

	public void markMappingFileAsAccessed() {
		prefs.setMappingFileLastAccess(System.currentTimeMillis());
	}

	public String getCategoryForTransaction(String description) {
		if (cache == null) {
			cache = new MapFileReader().read(getFile());
		}
		String category = null;

		for (String key : cache.keySet()) {
			if (description.toLowerCase().contains(key.toLowerCase())) {
				category = getFirstNonSpecialCategory(cache.get(key));
			}
		}
		return category;
	}

	public boolean isSubscription(String description) {
		if (cache == null) {
			cache = new MapFileReader().read(getFile());
		}
		for (String key : cache.keySet()) {
			if (description.toLowerCase().contains(key.toLowerCase())) {
				return cache.get(key).contains(CATEGORY_SUBSCRIPTION);
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

	private File getFile() {
		ClassLoader classLoader = getClass().getClassLoader();
		return new File(classLoader.getResource(MAPPING_FILE_NAME).getFile());
	}
}
