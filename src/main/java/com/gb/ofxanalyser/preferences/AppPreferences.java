package com.gb.ofxanalyser.preferences;

import java.util.prefs.Preferences;

import org.springframework.stereotype.Service;

@Service("prefs")
public class AppPreferences {

	private static final String PREF_MAPPING_FILE_ACCESS = "com.gb.ofxanalyser.util.PREF_MAPPING_FILE_ACCESS";

	private Preferences prefs;

	public AppPreferences() {
		prefs = Preferences.userRoot().node(AppPreferences.class.getName());
	}

	public long getMappingFileLastAccess() {
		return prefs.getLong(PREF_MAPPING_FILE_ACCESS, 0);
	}

	public void setMappingFileLastAccess(long timestamp) {
		prefs.putLong(PREF_MAPPING_FILE_ACCESS, timestamp);
	}
}
