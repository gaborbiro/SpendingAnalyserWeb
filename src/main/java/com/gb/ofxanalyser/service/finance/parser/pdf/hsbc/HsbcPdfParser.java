package com.gb.ofxanalyser.service.finance.parser.pdf.hsbc;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Map;

import com.gb.ofxanalyser.service.finance.parser.FileParser;
import com.gb.ofxanalyser.service.finance.parser.ParseException;
import com.gb.ofxanalyser.service.finance.parser.TransactionAggregate;
import com.gb.ofxanalyser.service.finance.parser.TransactionItem;
import com.gb.ofxanalyser.util.dynagrid.Grid;
import com.gb.ofxanalyser.util.dynagrid.Header;
import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;

public class HsbcPdfParser implements FileParser {

	public void parse(byte[] file, Map<TransactionItem, TransactionAggregate> transactionAggregate)
			throws ParseException {
		PdfReader reader = null;
		PdfStamper stamper = null;
		try {
			reader = new PdfReader(file);
			PdfReaderContentParser parser = new PdfReaderContentParser(reader);
			stamper = new PdfStamper(reader, new FileOutputStream("c:\\Downloads\\statement_out.pdf"));

			int i = 1;
			RowFinder finder1 = parser.processContent(i, new RowFinder("BALANCE BROUGHT FORWARD"));
			RowFinder finder2 = parser.processContent(i, new RowFinder("BALANCE CARRIED FORWARD"));

			if (finder1.getCount() == 1 && finder2.getCount() == 1 && finder1.getLly(0) > finder2.getLly(0)) {
				Rectangle2D.Float rect = new Rectangle2D.Float(0, finder2.getUry(0) + 1, Float.MAX_VALUE,
						finder1.getLly(0) - finder2.getUry(0) - 2);
				BoundedTextMarginFinder marginFinder = parser.processContent(i, new BoundedTextMarginFinder(rect));
				if (marginFinder.isFound()) {
					PdfContentByte cb = stamper.getOverContent(i);
					cb.setRGBColorStroke(255, 0, 0);
					cb.rectangle(marginFinder.getLlx(), marginFinder.getLly(), marginFinder.getWidth(),
							marginFinder.getHeight());
					cb.stroke();
				}

				BoundedTableFinder tableFinder = parser.processContent(i, new BoundedTableFinder(rect));
				Grid<String> table = tableFinder.getTable();

				if (table.size() > 0) {
					System.out.println("\n\n\n\n\n");
					Iterator<Header<String>> colHeaders = table.getColHeaders();
					Iterator<Header<String>> rowHeaders = table.getRowHeaders();
					String sep = "\t";

					for (Iterator<Iterator<String>> ti = table.iterator(); ti.hasNext();) {
						if (colHeaders.hasNext()) {
							System.out.print("\t");
							for (; colHeaders.hasNext();) {
								System.out.print(colHeaders.next().cells.size() + sep);
							}
							System.out.println();
						}
						if (rowHeaders.hasNext()) {
							System.out.print(rowHeaders.next().cells.size() + sep);
						}
						for (Iterator<String> si = ti.next(); si.hasNext();) {
							String data = si.next();
							data = data != null ? data.substring(0, Math.min(7, data.length())) : "_";
							System.out.print(data + sep);
						}
						System.out.println();
					}
				}
			}
			stamper.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
				stamper.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
