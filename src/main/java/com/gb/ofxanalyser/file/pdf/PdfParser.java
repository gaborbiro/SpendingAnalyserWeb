package com.gb.ofxanalyser.file.pdf;

import java.io.IOException;

public interface PdfParser {
	public Rect findText(int page, String text) throws IOException;

	public StringGrid findTable(int page, Rect rect) throws IOException;

	public int getNumberOfPages();

	public void close();
}
