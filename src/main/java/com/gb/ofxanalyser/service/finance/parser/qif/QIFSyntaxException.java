package com.gb.ofxanalyser.service.finance.parser.qif;

public class QIFSyntaxException extends Exception {

	public QIFSyntaxException(String message) {
		super("Error parsing QIF file: " + message);
	}
}
