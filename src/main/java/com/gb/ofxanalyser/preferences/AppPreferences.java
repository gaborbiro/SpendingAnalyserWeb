package com.gb.ofxanalyser.preferences;

import java.util.prefs.Preferences;

import org.springframework.stereotype.Service;

@Service("appPreferences")
public class AppPreferences {

	private static final String PREF_MAPPING_FILE_CHANGED_AT = "com.gb.ofxanalyser.util.PREF_MAPPING_FILE_CHANGED_AT";

	private Preferences prefs;

	public AppPreferences() {
		prefs = Preferences.userRoot().node(AppPreferences.class.getName());
	}

	public long getMappingFileLastModificationTime() {
		return prefs.getLong(PREF_MAPPING_FILE_CHANGED_AT, 0);
	}

	public void setMappingFileLastModificationTime(long timestamp) {
		prefs.putLong(PREF_MAPPING_FILE_CHANGED_AT, timestamp);
	}
}
