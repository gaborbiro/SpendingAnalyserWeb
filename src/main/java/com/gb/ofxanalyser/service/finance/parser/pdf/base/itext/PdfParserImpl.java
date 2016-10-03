package com.gb.ofxanalyser.service.finance.parser.pdf.base.itext;

import java.io.IOException;

import com.gb.ofxanalyser.service.finance.parser.Document;
import com.gb.ofxanalyser.service.finance.parser.pdf.base.PdfParser;
import com.gb.ofxanalyser.service.finance.parser.pdf.base.Rect;
import com.gb.ofxanalyser.service.finance.parser.pdf.base.StringGrid;
import com.gb.ofxanalyser.service.finance.parser.pdf.base.TextMatch;
import com.itextpdf.awt.geom.Rectangle2D;
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

	public TextMatch findText(int page, String text) throws IOException {
		RowFinder finder = parser.processContent(page, new RowFinder(text));
		if (finder.getCount() == 0) {
			return null;
		}
		return getTextMatchFromRowFinder(finder, 0);
	}

	public StringGrid findTable(int page, Rect rect) throws IOException {
		Rectangle2D.Float bounds = new Rectangle2D.Float(rect.x, rect.y, rect.width, rect.height);
		BoundedTableFinder tableFinder = parser.processContent(page, new BoundedTableFinder(bounds));
		return tableFinder.getTable();
	}

	private static TextMatch getTextMatchFromRowFinder(RowFinder finder, int i) {
		return new TextMatch(finder.getLlx(i), finder.getLly(i), finder.getUrx(i), finder.getUry(i), finder.getWidth(i),
				finder.getHeight(i));
	}

	public int getNumberOfPages() {
		return reader.getNumberOfPages();
	}

	public void close() {
		reader.close();
	}
}
