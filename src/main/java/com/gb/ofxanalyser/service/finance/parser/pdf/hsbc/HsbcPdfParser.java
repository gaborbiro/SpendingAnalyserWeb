package com.gb.ofxanalyser.service.finance.parser.pdf.hsbc;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.gb.ofxanalyser.service.finance.parser.FileParser;
import com.gb.ofxanalyser.service.finance.parser.ParseException;
import com.gb.ofxanalyser.service.finance.parser.TransactionAggregate;
import com.gb.ofxanalyser.service.finance.parser.TransactionItem;
import com.gb.ofxanalyser.util.dynagrid.Cell;
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

			for (int i = 1; i <= reader.getNumberOfPages(); i++) {
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
					Grid<Float, String> table = tableFinder.getTable();

					if (table.size() > 0) {
						System.out.println("\n\n\n\n\n");
						table.collapse(0f, 80.6f, new Grid.Collapse<String>() {

							public String collapse(List<String> items) {
								return items.stream().collect(Collectors.joining(" "));
							}
						});
						table.collapse(finder1.getLlx(0), 370f, new Grid.Collapse<String>() {

							public String collapse(List<String> items) {
								return items.stream().collect(Collectors.joining(" "));
							}
						});
						Iterator<Header<Float, String>> colHeaders = table.getColHeaders();
						Iterator<Header<Float, String>> rowHeaders = table.getRowHeaders();
						int sep = 3;

						for (Iterator<Iterator<Cell<Float, String>>> rowI = table.iterator(); rowI.hasNext();) {
							if (colHeaders.hasNext()) {
								System.out.print("\t");
								for (; colHeaders.hasNext();) {
									String text = String.format("%.2f", colHeaders.next().getIndex());
									System.out.print(text + sep(text, sep));
								}
								System.out.println();
								System.out.println();
							}
							if (rowHeaders.hasNext()) {
								System.out.print(rowHeaders.next().cells.size() + "\t");
							}
							for (Iterator<Cell<Float, String>> colI = rowI.next(); colI.hasNext();) {
								Cell<Float, String> cell = colI.next();
								String data = cell != null ? cell.data : null;
								data = data != null ? data : "_";
								System.out.print(data + sep(data, sep));
							}
							System.out.println();
						}
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

	private String sep(String text, int n) {
		int tabCount = (int) Math.ceil(((float) n * 8 - text.length()) / 8);
		String sep = "";
		for (int i = 0; i < tabCount; i++) {
			sep += "\t";
		}
		return sep;
	}
}
