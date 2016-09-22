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
//			for (int i = 4; i <= 4; i++) {
				RowFinder finderBrought = parser.processContent(i, new RowFinder("BALANCE BROUGHT FORWARD"));
				RowFinder finderCarried = parser.processContent(i, new RowFinder("BALANCE CARRIED FORWARD"));

				if (finderBrought.getCount() == 1 && finderCarried.getCount() == 1 && finderBrought.getLly(0) > finderCarried.getLly(0)) {
					Rectangle2D.Float rect = new Rectangle2D.Float(0, finderCarried.getUry(0) + 1, Float.MAX_VALUE,
							finderBrought.getLly(0) - finderCarried.getUry(0) - 2);
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
						RowFinder finderPaimentType = parser.processContent(i, new RowFinder("Payment type and details"));
						table.collapse(0f, finderPaimentType.getLlx(0) - 1, new Grid.Collapse<String>() {

							public String collapse(List<String> items) {
								return items.stream().collect(Collectors.joining(" "));
							}
						});
						
						int moneyColumnBuffer = 20;

						RowFinder finderPaidOut = parser.processContent(i, new RowFinder("Paid out"));
						table.collapse(finderBrought.getLlx(0), finderPaidOut.getLlx(0) - moneyColumnBuffer - 1, new Grid.Collapse<String>() {

							public String collapse(List<String> items) {
								return items.stream().collect(Collectors.joining(" "));
							}
						});
						table.collapse(finderPaidOut.getLlx(0) - moneyColumnBuffer, finderPaidOut.getLlx(0) + finderPaidOut.getWidth(0), new Grid.Collapse<String>() {

							public String collapse(List<String> items) {
								return items.stream().collect(Collectors.joining(" "));
							}
						});
						RowFinder finderPaidIn = parser.processContent(i, new RowFinder("Paid in"));
						table.collapse(finderPaidOut.getLlx(0) + finderPaidOut.getWidth(0) + 1, finderPaidIn.getLlx(0) + finderPaidIn.getWidth(0), new Grid.Collapse<String>() {

							public String collapse(List<String> items) {
								return items.stream().collect(Collectors.joining(" "));
							}
						});
						table.collapse(finderPaidIn.getLlx(0) + finderPaidIn.getWidth(0) + 1, null, new Grid.Collapse<String>() {

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
								data = data.substring(0, Math.min(sep * 7, data.length()));
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
