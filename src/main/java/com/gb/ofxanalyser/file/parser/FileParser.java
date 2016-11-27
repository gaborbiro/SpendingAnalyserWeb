package com.gb.ofxanalyser.file.parser;

/**
 * Interface for all parsers (HSBC, REVOLUT, OFX, QIF...)
 */
public interface FileParser {

	public interface FileEntrySink {

		/**
		 * Make sure different sinks don't block each other.
		 */
		public void onEntry(FileContent file, FileEntry entry);
	}

	public int parse(FileContent file, FileEntrySink listener) throws ParseException;

	public String getConverterName();
}
