package com.gb.ofxanalyser.service.finance.parser.pdf.hsbc;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import com.gb.ofxanalyser.service.finance.parser.FileParser;
import com.gb.ofxanalyser.service.finance.parser.ParseException;
import com.gb.ofxanalyser.service.finance.parser.TransactionAggregate;
import com.gb.ofxanalyser.service.finance.parser.TransactionItem;
import com.gb.ofxanalyser.util.dynagrid.Cell;
import com.gb.ofxanalyser.util.dynagrid.StringGrid;
import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;

public class HsbcPdfParser implements FileParser {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yy");

	private static final String ANCHOR_TABLE_START = "BALANCE BROUGHT FORWARD";
	private static final String ANCHOR_TABLE_END = "BALANCE CARRIED FORWARD";
	private static final String ANCHOR_PAY_TYPE_DETAILS = "Payment type and details";
	private static final String ANCHOR_PAYED_OUT = "Paid out";
	private static final String ANCHOR_PAYED_IN = "Paid in";

	/**
	 * Don't know where the description column ends and the Paid in/out column
	 * starts. Increase this value if there are like 4-5 digit values in the pdf
	 */
	private static final int MONEY_COLUMN_LOOKBACK = 20;

	public void parse(byte[] file, Map<TransactionItem, TransactionAggregate> transactionAggregate)
			throws ParseException {
		PdfReader reader = null;
		try {
			reader = new PdfReader(file);
			PdfReaderContentParser parser = new PdfReaderContentParser(reader);
			Date currentDate = null;

			for (int i = 1; i <= reader.getNumberOfPages(); i++) {
				// find the table-part of the page
				RowFinder finderBrought = parser.processContent(i, new RowFinder(ANCHOR_TABLE_START));
				RowFinder finderCarried = parser.processContent(i, new RowFinder(ANCHOR_TABLE_END));

				if (finderBrought.getCount() == 1 && finderCarried.getCount() == 1
						&& finderBrought.getLly(0) > finderCarried.getLly(0)) {
					Rectangle2D.Float rect = new Rectangle2D.Float(0, finderCarried.getUry(0) + 1, Float.MAX_VALUE,
							finderBrought.getLly(0) - finderCarried.getUry(0) - 2);
					// convert the pdf to a table
					BoundedTableFinder tableFinder = parser.processContent(i, new BoundedTableFinder(rect));
					StringGrid table = tableFinder.getTable();

					if (table.size() > 0) {
						// at this point, every word that has a unique x
						// coordinate sits in its own separate column and
						// doesn't form a useful sentence

						// concatenate the date columns
						RowFinder finderPaimentType = parser.processContent(i, new RowFinder(ANCHOR_PAY_TYPE_DETAILS));
						table.collapse(0f, finderPaimentType.getLlx(0) - 1);

						// concatenate the name/description columns
						RowFinder finderPaidOut = parser.processContent(i, new RowFinder(ANCHOR_PAYED_OUT));
						table.collapse(finderBrought.getLlx(0), finderPaidOut.getLlx(0) - MONEY_COLUMN_LOOKBACK - 1);

						// concatenate the Paid out columns
						table.collapse(finderPaidOut.getLlx(0) - MONEY_COLUMN_LOOKBACK,
								finderPaidOut.getLlx(0) + finderPaidOut.getWidth(0));

						// concatenate the Paid in columns
						RowFinder finderPaidIn = parser.processContent(i, new RowFinder(ANCHOR_PAYED_IN));
						table.collapse(finderPaidOut.getLlx(0) + finderPaidOut.getWidth(0) + 1,
								finderPaidIn.getLlx(0) + finderPaidIn.getWidth(0));

						// concatenate the Balance columns
						table.collapse(finderPaidIn.getLlx(0) + finderPaidIn.getWidth(0) + 1, null);

						String description = "";

						for (Iterator<Iterator<Cell<Float, String>>> rowI = table.iterator(); rowI.hasNext();) {
							int colIndex = 0;
							Double amount = null;

							for (Iterator<Cell<Float, String>> colI = rowI.next(); colI.hasNext();) {
								Cell<Float, String> cell = colI.next();
								String data = cell != null ? cell.data : "";

								switch (colIndex) {
								case 0:
									// date
									try {
										currentDate = DATE_FORMAT.parse(data);
									} catch (java.text.ParseException e) {
									}
									break;
								case 1:
									// transaction type
									// don't care
									break;
								case 2:
									// description
									description += data + " ";
									break;
								case 3:
									// paid out
									try {
										amount = -NumberFormat.getInstance().parse(data).doubleValue();
									} catch (java.text.ParseException e) {
									}
									break;
								case 4:
									// paid in
									try {
										amount = NumberFormat.getInstance().parse(data).doubleValue();
									} catch (java.text.ParseException e) {
									}
									break;
								case 5:
									// balance
									// don't care
									break;
								default:
									break;
								}
								colIndex++;
							}
							if (amount != null) {
								TransactionItem transactionItem = new TransactionItem(currentDate, "H " + description,
										amount);
								transactionAggregate.put(transactionItem,
										new TransactionAggregate(amount, transactionItem));
								amount = null;
								description = "";
							}
						}
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
