package com.gb.ofxanalyser.service.finance.parser.hsbc;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.gb.ofxanalyser.service.finance.parser.ParseException;
import com.gb.ofxanalyser.service.finance.parser.TransactionExtractor;
import com.gb.ofxanalyser.service.finance.parser.TransactionItem;
import com.gb.ofxanalyser.service.finance.parser.pdf.PdfParser;
import com.gb.ofxanalyser.service.finance.parser.pdf.Rect;
import com.gb.ofxanalyser.service.finance.parser.pdf.StringGrid;
import com.gb.ofxanalyser.util.dynagrid.Cell;

public class HsbcParser implements TransactionExtractor {

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

	private PdfParser pdfParser;

	public HsbcParser(PdfParser pdfParser) {
		this.pdfParser = pdfParser;
	}

	public List<TransactionItem> getTransactions() throws ParseException {
		List<TransactionItem> result = new ArrayList<TransactionItem>();
		try {
			Date currentDate = null;

			for (int i = 1; i <= pdfParser.getNumberOfPages(); i++) {
				// find the table-part of the page
				Rect topDelimiter = pdfParser.findText(i, ANCHOR_TABLE_START);
				Rect bottomDelimiter = pdfParser.findText(i, ANCHOR_TABLE_END);

				if (topDelimiter != null && bottomDelimiter != null
						&& topDelimiter.getBottom() > bottomDelimiter.getTop()) {
					Rect rect = new Rect(0f, bottomDelimiter.getTop() + 1, Float.MAX_VALUE, topDelimiter.getBottom() - 1);
					// convert the pdf to a table
					StringGrid table = pdfParser.findTable(i, rect);

					if (table.size() > 0) {
						// at this point, every word that has a unique x
						// coordinate sits in its own separate column and
						// doesn't form a useful sentence

						// concatenate the date columns
						Rect finderPaimentType = pdfParser.findText(i, ANCHOR_PAY_TYPE_DETAILS);
						table.collapse(0f, finderPaimentType.getLeft() - 1);

						// concatenate the name/description columns
						Rect finderPaidOut = pdfParser.findText(i, ANCHOR_PAYED_OUT);
						table.collapse(topDelimiter.getLeft(), finderPaidOut.getLeft() - MONEY_COLUMN_LOOKBACK - 1);

						// concatenate the Paid out columns
						table.collapse(finderPaidOut.getLeft() - MONEY_COLUMN_LOOKBACK,
								finderPaidOut.getLeft() + finderPaidOut.getWidth());

						// concatenate the Paid in columns
						Rect finderPaidIn = pdfParser.findText(i, ANCHOR_PAYED_IN);
						table.collapse(finderPaidOut.getRight() + 1, finderPaidIn.getRight());

						// concatenate the Balance columns
						table.collapse(finderPaidIn.getLeft() + finderPaidIn.getWidth() + 1, null);

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
								result.add(transactionItem);
								amount = null;
								description = "";
							}
						}
					}
				}
			}
			pdfParser.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pdfParser.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
