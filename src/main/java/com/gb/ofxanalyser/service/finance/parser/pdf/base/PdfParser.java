package com.gb.ofxanalyser.service.finance.parser.pdf.base;

import java.io.IOException;

public interface PdfParser {
	public TextMatch findText(int page, String text) throws IOException;

	public StringGrid findTable(int page, Rect rect) throws IOException;

	public int getNumberOfPages();

	public void close();
}
