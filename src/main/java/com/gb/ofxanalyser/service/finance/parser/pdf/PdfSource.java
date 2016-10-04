package com.gb.ofxanalyser.service.finance.parser.pdf;

import java.io.IOException;

public interface PdfSource {
	public Rect findText(int page, String text) throws IOException;

	public StringGrid findTable(int page, Rect rect) throws IOException;

	public int getNumberOfPages();

	public void close();
}
