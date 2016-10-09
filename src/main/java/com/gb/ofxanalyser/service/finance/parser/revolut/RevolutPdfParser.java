package com.gb.ofxanalyser.service.finance.parser.revolut;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
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
import com.gb.ofxanalyser.util.dynagrid.Cell;

public class RevolutPdfParser implements TransactionExtractor {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");

	private static final String ANCHOR_START = "C o m p l e t e d D a t e";
	private static final String ANCHOR_END = "P l e a s e N o t e:";
	private static final String ANCHOR_REFERENCE = "R e f e r e n c e";
	private static final String ANCHOR_PAYED_OUT = "P a i d O u t";
	private static final String ANCHOR_PAYED_IN = "P a i d I n";

	private static final String COLLAPSE_SEPARATOR = "";
	
	private Rect referenceDelimiter;
	private Rect paidOutDelimiter;
	private Rect paidInDelimiter;

	/**
	 * Don't know where the payee column ends and the Paid in/out column
	 * starts. Increase this value if there are like 4-5 digit values in the pdf
	 */
	private static final int MONEY_COLUMN_LOOKBACK = 20;

	public List<TransactionItem> getTransactions(Document file) throws ParseException {
		List<TransactionItem> result = new ArrayList<TransactionItem>();
		PdfParser parser = new PdfParserImpl(file);
		referenceDelimiter = null;
		paidOutDelimiter = null;
		paidInDelimiter = null;
		try {
			for (int i = 1; i <= parser.getNumberOfPages(); i++) {
				result.addAll(handlePage(parser, i));
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

	private Collection<TransactionItem> handlePage(PdfParser parser, int page) throws IOException {
		Rect topDelimiter = parser.findText(page, ANCHOR_START);
		Rect bottomDelimiter = parser.findText(page, ANCHOR_END);

		Rect rect;
		if (topDelimiter != null) {
			// first page of the pdf
			if (bottomDelimiter != null) {
				rect = new Rect(0f, bottomDelimiter.getTop() + 1, Float.MAX_VALUE, topDelimiter.getBottom() - 1);
			} else {
				rect = new Rect(0f, 0f, Float.MAX_VALUE, topDelimiter.getBottom() - 1);
			}
		} else if (bottomDelimiter != null) {
			rect = new Rect(0f, bottomDelimiter.getTop() + 1, Float.MAX_VALUE, Float.MAX_VALUE);
		} else {
			rect = new Rect(0f, 0f, Float.MAX_VALUE, Float.MAX_VALUE);
		}

		// convert the pdf to a table
		StringGrid table = parser.findTable(page, rect);

		if (table.size() > 0) {
			// at this point, every word that has a unique x
			// coordinate sits in its own separate column and
			// doesn't form a useful sentence

			if (topDelimiter != null) {
				referenceDelimiter = parser.findText(page, ANCHOR_REFERENCE);
				paidOutDelimiter = parser.findText(page, ANCHOR_PAYED_OUT);
				paidInDelimiter = parser.findText(page, ANCHOR_PAYED_IN);
			}

			// concatenate the Date columns
			table.collapse(0f, referenceDelimiter.getLeft() - 1, COLLAPSE_SEPARATOR);

			// concatenate the Reference columns
			table.collapse(referenceDelimiter.getLeft(), paidOutDelimiter.getLeft() - MONEY_COLUMN_LOOKBACK - 1, COLLAPSE_SEPARATOR);

			// concatenate the Paid out columns
			table.collapse(paidOutDelimiter.getLeft() - MONEY_COLUMN_LOOKBACK,
					paidOutDelimiter.getLeft() + paidOutDelimiter.getWidth(), COLLAPSE_SEPARATOR);

			// concatenate the Paid in columns
			table.collapse(paidOutDelimiter.getRight() + 1, paidInDelimiter.getRight(), COLLAPSE_SEPARATOR);

			// concatenate the Balance columns
			table.collapse(paidInDelimiter.getLeft() + paidInDelimiter.getWidth() + 1, null, COLLAPSE_SEPARATOR);

			return processTable(page, table);
		}
		return null;
	}

	private Collection<TransactionItem> processTable(int page, StringGrid table) {
		List<TransactionItem> result = new ArrayList<TransactionItem>();

		for (Iterator<Iterator<Cell<Float, String>>> rowI = table.iterator(); rowI.hasNext();) {
			TransactionItem transactionItem = processRow(rowI);

			if (transactionItem != null) {
				result.add(transactionItem);
			}
		}
		return result;
	}

	private TransactionItem processRow(Iterator<Iterator<Cell<Float, String>>> rowI) {
		int colIndex = 0;
		Date date = null;
		String description = null;
		double amount = 0;

		for (Iterator<Cell<Float, String>> colI = rowI.next(); colI.hasNext();) {
			Cell<Float, String> cell = colI.next();
			String data = cell != null ? cell.data : "";

			switch (colIndex) {
			case 0:
				// date
				try {
					date = DATE_FORMAT.parse(data);
				} catch (java.text.ParseException e) {
				}
				break;
			case 1:
				// payee
				description = data + " ";
				break;
			case 2:
				// paid out
				try {
					amount = -NumberFormat.getInstance().parse(data).doubleValue();
				} catch (java.text.ParseException e) {
				}
				break;
			case 3:
				// paid in
				try {
					amount = NumberFormat.getInstance().parse(data).doubleValue();
				} catch (java.text.ParseException e) {
				}
				break;
			case 4:
				// balance
				// don't care
				break;
			default:
				break;
			}
			colIndex++;
		}

		if (date != null) {
			return new TransactionItem(date, "R " + description, null, amount);
		} else {
			return null;
		}
	}

	@Override
	public String getParserName() {
		return "Revolut PDF Parser";
	}
}
