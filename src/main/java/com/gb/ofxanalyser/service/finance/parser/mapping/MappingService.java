package com.gb.ofxanalyser.service.finance.parser.mapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A database that maps transaction payees onto spending categories.<br>
 * Like "STARBUCKS" to "Food & Drink" or "DENPLAN" to "Healthcare"<br>
 */
public class MappingService {

	public static final String CATEGORY_SUBSCRIPTION = "Subscription";
	public static final String CATEGORY_UNKNOWN = "Unknown";

	private static Map<String, List<String>> CACHE;

	public File getMappingResourceFile() {
		ClassLoader classLoader = getClass().getClassLoader();
		return new File(classLoader.getResource("categories").getFile());
	}

	public static File getLocalMappingFile() {
		return new File("c:\\Work2\\Spring\\ofx-analyser-web\\src\\main\\resources\\categories");
	}

	public static Map<String, List<String>> getMapping() {
		return new MappingService().readMappings(getLocalMappingFile());
	}

	public Map<String, List<String>> readMappings(File file) {
		if (CACHE != null) {
			return CACHE;
		}

		CACHE = new HashMap<String, List<String>>();
		InputStream is = null;
		BufferedReader bfReader = null;
		try {
			is = new FileInputStream(file);
			bfReader = new BufferedReader(new InputStreamReader(is));
			String name = null;
			String line = null;
			List<String> categories = null;

			while ((line = bfReader.readLine()) != null) {
				if (name != null) {
					categories = CACHE.get(name);
				}

				if (categories == null) {
					categories = new ArrayList<String>();
				}

				if (line.startsWith("==")) {
					String[] tokens = line.substring(2).split(",");

					for (String token : tokens) {
						token = token.trim();

						if (!categories.contains(token)) {
							categories.add(token);
						}
					}
				} else {
					name = line;
					categories = null;
				}

				if (name != null) {
					CACHE.put(name, categories);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception ex) {
			}
		}
		return CACHE;
	}

	public String getCategoryForSpending(String description) {
		Map<String, List<String>> mapping = getMapping();

		String category = null;

		for (String key : mapping.keySet()) {
			if (description.toLowerCase().contains(key.toLowerCase())) {
				category = getFirstNonSpecialCategory(mapping.get(key));
			}
		}
		if (category == null) {
			category = CATEGORY_UNKNOWN;
		}
		return category;
	}

	public boolean isSubscription(String description) {
		Map<String, List<String>> mapping = getMapping();

		for (String key : mapping.keySet()) {
			if (description.toLowerCase().contains(key.toLowerCase())) {
				return mapping.get(key).contains(CATEGORY_SUBSCRIPTION);
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

	public static void invalidateCache() {
		CACHE = null;
	}
}
