package com.gb.ofxanalyser.service.file.parser;

/**
 * Interface for all parsers (HSBC, REVOLUT, OFX, QIF...)
 */
public interface FileParser<E> {

	public interface FileEntrySink<E> {
		
		/**
		 * Make sure different sinks don't block each other.
		 */
		public void onEntry(FileContent file, E entry);
	}

	public int parse(FileContent file, FileEntrySink<E> listener) throws ParseException;

	public String getConverterName();
}
