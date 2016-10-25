package com.gb.ofxanalyser.service.finance.parser.categories;

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

public class CategoryParser {

	public static Map<String, List<String>> readCategories() {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		ClassLoader classLoader = CategoryParser.class.getClassLoader();
		File file = new File(classLoader.getResource("src/main/resources/categories").getFile());
		InputStream is = null;
		BufferedReader bfReader = null;
		try {
			is = new FileInputStream(file);
			bfReader = new BufferedReader(new InputStreamReader(is));
			String name = null;
			String line = null;
			while ((line = bfReader.readLine()) != null) {
				List<String> categories = null;

				if (name != null) {
					categories = result.get(name);

					if (categories == null) {
						categories = new ArrayList<String>();
						result.put(name, categories);
					}
				}

				if (line.startsWith("==")) {
					String[] tokens = line.substring(2).split(",");

					for (String token : tokens) {
						token = token.trim().toLowerCase();
						
						if (!categories.contains(token)) {
							categories.add(token);
						}
					}
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
		return result;
	}
}
