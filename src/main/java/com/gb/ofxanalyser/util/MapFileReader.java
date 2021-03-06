package com.gb.ofxanalyser.util;

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

public class MapFileReader {

	public Map<String, List<String>> read(File file) {
		HashMap<String, List<String>> CACHE = new HashMap<String, List<String>>();
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

}
