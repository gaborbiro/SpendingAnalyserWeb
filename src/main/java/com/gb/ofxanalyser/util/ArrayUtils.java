package com.gb.ofxanalyser.util;

public class ArrayUtils {

	public static String toString(Object[] array, String separator) {
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < array.length; i++) {
			buffer.append(array[i] != null ? array[i] : "");

			if (i < array.length - 1) {
				buffer.append(separator);
			}
		}

		return buffer.toString();
	}
}
