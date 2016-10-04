package com.gb.ofxanalyser.service.finance.parser.pdf.itext;

import java.io.IOException;

import com.gb.ofxanalyser.service.finance.parser.Document;
import com.gb.ofxanalyser.service.finance.parser.pdf.PdfParser;
import com.gb.ofxanalyser.service.finance.parser.pdf.Rect;
import com.gb.ofxanalyser.service.finance.parser.pdf.StringGrid;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;

public class PdfParserImpl implements PdfParser {

	private PdfReader reader;
	private PdfReaderContentParser parser;

	public PdfParserImpl(Document file) {
		try {
			reader = new PdfReader(file.getContent());
			parser = new PdfReaderContentParser(reader);
		} catch (IOException e) {
			throw new RuntimeException("Error creating " + PdfParserImpl.class.getName(), e);
		}
	}

	public Rect findText(int page, String text) throws IOException {
		RowFinder finder = parser.processContent(page, new RowFinder(text));
		if (finder.getCount() == 0) {
			return null;
		}
		return getTextMatchFromRowFinder(finder, 0);
	}

	public StringGrid findTable(int page, Rect rect) throws IOException {
		BoundedTableFinder tableFinder = parser.processContent(page, new BoundedTableFinder(rect));
		return tableFinder.getTable();
	}

	private static Rect getTextMatchFromRowFinder(RowFinder finder, int i) {
		return new Rect(finder.getLlx(i), finder.getLly(i), finder.getUrx(i), finder.getUry(i));
	}

	public int getNumberOfPages() {
		return reader.getNumberOfPages();
	}

	public void close() {
		reader.close();
	}
}
