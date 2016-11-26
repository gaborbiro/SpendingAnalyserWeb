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

	@Autowired
	AppPreferences appPreferences;

	private Map<String, List<String>> cache;

	private static File getMappingFile() {
		return getLocalMappingFile();
	}

	public boolean hasCategoriesFileChanged() {
		return getMappingFile().lastModified() > appPreferences.getMappingFileLastModificationTime();
	}

	public String getCategoryForTransaction(String description) {
		if (cache == null) {
			cache = new MapFileReader().read(getMappingFile());
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
			cache = new MapFileReader().read(getMappingFile());
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

	private File getMappingResourceFile() {
		ClassLoader classLoader = getClass().getClassLoader();
		return new File(classLoader.getResource("categories").getFile());
	}

	private static File getLocalMappingFile() {
		return new File("c:\\Work2\\Spring\\ofx-analyser-web\\src\\main\\resources\\categories");
	}
}
