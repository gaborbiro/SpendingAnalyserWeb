package com.gb.ofxanalyser.file.parser.revolut;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import com.gb.ofxanalyser.file.parser.FileContent;
import com.gb.ofxanalyser.file.parser.FileEntry;
import com.gb.ofxanalyser.file.parser.FileParser;
import com.gb.ofxanalyser.file.parser.ParseException;
import com.gb.ofxanalyser.file.pdf.PdfParser;
import com.gb.ofxanalyser.file.pdf.Rect;
import com.gb.ofxanalyser.file.pdf.StringGrid;
import com.gb.ofxanalyser.file.pdf.dynagrid.Cell;
import com.gb.ofxanalyser.file.pdf.itext.PdfParserImpl;

public class RevolutPdfParser implements FileParser {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");

	private static final String ANCHOR_START = "Completed Date";
	private static final String ANCHOR_END = "Please Note:";
	private static final String ANCHOR_REFERENCE = "Reference";
	private static final String ANCHOR_PAYED_OUT = "Paid Out";
	private static final String ANCHOR_PAYED_IN = "Paid In";

	private static final String COLLAPSE_SEPARATOR = "";

	private Rect referenceDelimiter;
	private Rect paidOutDelimiter;
	private Rect paidInDelimiter;

	/**
	 * Don't know where the payee column ends and the Paid in/out column starts.
	 * Increase this value if there are like 4-5 digit values in the pdf
	 */
	private static final int MONEY_COLUMN_LOOKBACK = 20;

	public int parse(FileContent file, FileEntrySink listener) throws ParseException {
		PdfParser parser = new PdfParserImpl(file);
		referenceDelimiter = null;
		paidOutDelimiter = null;
		paidInDelimiter = null;
		int entryCount = 0;
		try {
			for (int i = 1; i <= parser.getNumberOfPages(); i++) {
				entryCount += handlePage(parser, file, i, listener);
			}
			parser.close();
		} catch (Exception e) {
			throw new ParseException(e.getMessage());
		} finally {
			try {
				parser.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return entryCount;
	}

	private int handlePage(PdfParser parser, FileContent file, int page, FileEntrySink listener) throws IOException {
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
			table.collapse(referenceDelimiter.getLeft(), paidOutDelimiter.getLeft() - MONEY_COLUMN_LOOKBACK - 1,
					COLLAPSE_SEPARATOR);

			// concatenate the Paid out columns
			table.collapse(paidOutDelimiter.getLeft() - MONEY_COLUMN_LOOKBACK,
					paidOutDelimiter.getLeft() + paidOutDelimiter.getWidth(), COLLAPSE_SEPARATOR);

			// concatenate the Paid in columns
			table.collapse(paidOutDelimiter.getRight() + 1, paidInDelimiter.getRight(), COLLAPSE_SEPARATOR);

			// concatenate the Balance columns
			table.collapse(paidInDelimiter.getLeft() + paidInDelimiter.getWidth() + 1, null, COLLAPSE_SEPARATOR);

			return processTable(file, page, table, listener);
		}
		return 0;
	}

	private int processTable(FileContent file, int page, StringGrid table, FileEntrySink listener) {
		int count = 0;
		int row = 0;
		for (Iterator<Iterator<Cell<Float, String>>> rowI = table.iterator(); rowI.hasNext();) {
			FileEntry entry = processRow(page, row++, rowI);

			if (entry != null) {
				listener.onEntry(file, entry);
				count++;
			}
		}
		return count;
	}

	private FileEntry processRow(int page, int row, Iterator<Iterator<Cell<Float, String>>> rowI) {
		int colIndex = 0;
		Date date = null;
		String description = null;
		double amount = 0;
//		StringBuffer debugBuffer = new StringBuffer();

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
//				debugBuffer.append(description);
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
//		System.out.print(page + "/" + row + ": " + debugBuffer.toString());
		if (date != null) {
//			System.out.println(" success");
			return new FileEntry(date, description, null, amount);
		} else {
//			System.out.println(" fail");
			return null;
		}
	}

	@Override
	public String getConverterName() {
		return "Revolut PDF Parser";
	}
}
