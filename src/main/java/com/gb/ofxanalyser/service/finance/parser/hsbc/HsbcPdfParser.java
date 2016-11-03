package com.gb.ofxanalyser.service.finance.parser.hsbc;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.gb.ofxanalyser.service.finance.parser.Document;
import com.gb.ofxanalyser.service.finance.parser.ParseException;
import com.gb.ofxanalyser.service.finance.parser.TransactionExtractor;
import com.gb.ofxanalyser.service.finance.parser.TransactionItem;
import com.gb.ofxanalyser.service.finance.parser.pdf.PdfParser;
import com.gb.ofxanalyser.service.finance.parser.pdf.Rect;
import com.gb.ofxanalyser.service.finance.parser.pdf.StringGrid;
import com.gb.ofxanalyser.service.finance.parser.pdf.itext.PdfParserImpl;
import com.gb.ofxanalyser.util.TextUtils;
import com.gb.ofxanalyser.util.dynagrid.Cell;

public class HsbcPdfParser implements TransactionExtractor {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yy");

	private static final String ANCHOR_TABLE_START = "BALANCE BROUGHT FORWARD";
	private static final String ANCHOR_TABLE_END = "BALANCE CARRIED FORWARD";
	private static final String ANCHOR_PAY_TYPE_DETAILS = "Payment type and details";
	private static final String ANCHOR_PAYED_OUT = "Paid out";
	private static final String ANCHOR_PAYED_IN = "Paid in";

	private static final String COLLAPSE_SEPARATOR = " ";

	/**
	 * Don't know where the payee column ends and the Paid in/out column starts.
	 * Increase this value if there are like 4-5 digit values in the pdf
	 */
	private static final int MONEY_COLUMN_LOOKBACK = 20;

	public List<TransactionItem> getTransactions(Document file) throws ParseException {
		List<TransactionItem> result = new ArrayList<TransactionItem>();
		PdfParser parser = new PdfParserImpl(file);
		try {
			FileContext context = new FileContext();

			for (int i = 1; i <= parser.getNumberOfPages(); i++) {
				result.addAll(handlePage(parser, i, context));
			}
			parser.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				parser.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	private static List<TransactionItem> handlePage(PdfParser parser, int page, FileContext fileContext)
			throws IOException {
		// find the table-part of the page
		Rect topDelimiter = parser.findText(page, ANCHOR_TABLE_START);
		Rect bottomDelimiter = parser.findText(page, ANCHOR_TABLE_END);

		if (topDelimiter != null && bottomDelimiter != null && topDelimiter.getBottom() > bottomDelimiter.getTop()) {
			Rect rect = new Rect(0f, bottomDelimiter.getTop() + 1, Float.MAX_VALUE, topDelimiter.getBottom() - 1);
			// convert the pdf to a table
			StringGrid table = parser.findTable(page, rect);

			if (table.size() > 0) {
				// at this point, every word that has a unique x
				// coordinate sits in its own separate column and
				// doesn't form a useful sentence

				// concatenate the Date columns
				Rect finderPaimentType = parser.findText(page, ANCHOR_PAY_TYPE_DETAILS);
				table.collapse(0f, finderPaimentType.getLeft() - 1, COLLAPSE_SEPARATOR);

				// concatenate the Name/Description columns
				Rect finderPaidOut = parser.findText(page, ANCHOR_PAYED_OUT);
				table.collapse(topDelimiter.getLeft(), finderPaidOut.getLeft() - MONEY_COLUMN_LOOKBACK - 1,
						COLLAPSE_SEPARATOR);

				// concatenate the Paid out columns
				table.collapse(finderPaidOut.getLeft() - MONEY_COLUMN_LOOKBACK,
						finderPaidOut.getLeft() + finderPaidOut.getWidth(), COLLAPSE_SEPARATOR);

				// concatenate the Paid in columns
				Rect finderPaidIn = parser.findText(page, ANCHOR_PAYED_IN);
				table.collapse(finderPaidOut.getRight() + 1, finderPaidIn.getRight(), COLLAPSE_SEPARATOR);

				// concatenate the Balance columns
				table.collapse(finderPaidIn.getLeft() + finderPaidIn.getWidth() + 1, null, COLLAPSE_SEPARATOR);

				return processTable(page, table, fileContext);
			}
		}
		return new ArrayList<TransactionItem>(0);
	}

	private static List<TransactionItem> processTable(int page, StringGrid table, FileContext fileContext) {
		List<TransactionItem> result = new ArrayList<TransactionItem>();
		TransactionContext transactionContext = new TransactionContext(null, "", null);

		for (Iterator<Iterator<Cell<Float, String>>> rowI = table.iterator(); rowI.hasNext();) {
			processRow(rowI, fileContext, transactionContext);

			if (transactionContext.amount != null) {
				TransactionItem transactionItem = new TransactionItem(fileContext.currentDate, transactionContext.payee,
						transactionContext.memo, transactionContext.amount);
				result.add(transactionItem);
				transactionContext = new TransactionContext(null, "", null);
			}
		}
		return result;
	}

	private static void processRow(Iterator<Iterator<Cell<Float, String>>> rowI, FileContext fileContext,
			TransactionContext transactionContext) {
		int colIndex = 0;

		for (Iterator<Cell<Float, String>> colI = rowI.next(); colI.hasNext();) {
			processCell(colIndex++, colI, fileContext, transactionContext);
		}
	}

	private static void processCell(int colIndex, Iterator<Cell<Float, String>> colI, FileContext fileContext,
			TransactionContext transactionContext) {
		Cell<Float, String> cell = colI.next();
		String data = cell != null ? cell.data : "";

		switch (colIndex) {
		case 0:
			// date
			try {
				fileContext.currentDate = DATE_FORMAT.parse(data);
			} catch (java.text.ParseException e) {
			}
			break;
		case 1:
			// transaction type
			// don't care
			break;
		case 2:
			// payee
			if (TextUtils.isEmpty(transactionContext.payee)) {
				transactionContext.payee = data;
			} else {
				transactionContext.memo += data + " ";
			}
			break;
		case 3:
			// paid out
			try {
				transactionContext.amount = -NumberFormat.getInstance().parse(data).doubleValue();
			} catch (java.text.ParseException e) {
			}
			break;
		case 4:
			// paid in
			try {
				transactionContext.amount = NumberFormat.getInstance().parse(data).doubleValue();
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
	}

	/**
	 * Context that scopes across all pages of the pdf file because the
	 * transactions in one day may span over multiple pages
	 */
	private static class FileContext {
		public Date currentDate;
	}

	/**
	 * Context that scopes across multiple rows because the a transaction may
	 * span over multiple rows
	 */
	private static class TransactionContext {
		public String payee;
		public String memo;
		public Double amount;

		public TransactionContext(String payee, String memo, Double amount) {
			this.payee = payee;
			this.memo = memo;
			this.amount = amount;
		}
	}

	@Override
	public String getParserName() {
		return "HSBC PDF Parser";
	}
}
