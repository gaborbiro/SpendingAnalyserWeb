package com.gb.ofxanalyser.service.file.parser.qif;

public class QIFSyntaxException extends Exception {

	private static final long serialVersionUID = 1L;

	public QIFSyntaxException(String message) {
		super("Error parsing QIF file: " + message);
	}
}
