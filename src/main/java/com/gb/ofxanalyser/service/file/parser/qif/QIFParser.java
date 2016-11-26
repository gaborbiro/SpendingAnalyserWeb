package com.gb.ofxanalyser.service.file.parser.qif;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import com.gb.ofxanalyser.service.file.parser.FileContent;
import com.gb.ofxanalyser.service.file.parser.FileEntry;
import com.gb.ofxanalyser.service.file.parser.FileParser;
import com.gb.ofxanalyser.service.file.parser.ParseException;
import com.gb.ofxanalyser.util.TextUtils;

public class QIFParser implements FileParser {

	public static final TimeZone GMT_TIME_ZONE = TimeZone.getTimeZone("GMT");

	private static final char TOKEN_TYPE = '!';
	private static final char TOKEN_DATE = 'D';
	private static final char TOKEN_AMOUNT1 = 'U';
	private static final char TOKEN_AMOUNT2 = 'T';
	private static final char TOKEN_PAYEE = 'P';
	private static final char TOKEN_ADDRESS = 'A';
	private static final char TOKEN_MEMO = 'M';
	private static final char TOKEN_CATEGORY = 'L';
	private static final char TOKEN_SPLIT_CATEGORY = 'S';
	private static final char TOKEN_SPLIT_MEMO = 'E';
	private static final char TOKEN_SPLIT_AMOUNT = '$';
	private static final char TOKEN_END = '^';

	@Override
	public int parse(FileContent file, FileEntrySink listener) throws ParseException {
		InputStream is = null;
		BufferedReader bfReader = null;
		int entryCount = 0;
		try {
			is = new ByteArrayInputStream(file.getContent());
			bfReader = new BufferedReader(new InputStreamReader(is));
			TransactionContext context = new TransactionContext();
			String line = null;
			while ((line = bfReader.readLine()) != null) {
				entryCount += processLine(file, context, line, listener);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new ParseException(e.getMessage());
		} catch (QIFSyntaxException e) {
			e.printStackTrace();
			throw new ParseException(e.getMessage());
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception ex) {
			}
		}
		return entryCount;
	}

	private int processLine(FileContent file, TransactionContext context, String line, FileEntrySink listener)
			throws QIFSyntaxException {
		int entryCount = 0;
		char c = line.charAt(0);
		line = line.substring(1);
		switch (c) {
		case TOKEN_TYPE:

			break;
		case TOKEN_DATE:
			int year = Integer.parseInt(line.substring(6, 10));
			int month = Integer.parseInt(line.substring(3, 5)) - 1; // java
																	// month
																	// numbers
																	// are
																	// zero-based
			int day = Integer.parseInt(line.substring(0, 2));
			// set up a new calendar at zero, then set all the fields.
			GregorianCalendar calendar = new GregorianCalendar(year, month, day, 0, 0, 0);
			calendar.setTimeZone(GMT_TIME_ZONE);
			context.date = calendar.getTime();
			break;
		case TOKEN_AMOUNT1:
			context.amount = Double.parseDouble(line);
			break;
		case TOKEN_AMOUNT2:
			if (context.amount == null) {
				context.amount = Double.parseDouble(line);
			}
			break;
		case TOKEN_PAYEE:
			context.payee = line;
			break;
		case TOKEN_MEMO:
			context.memo = line;
			break;
		case TOKEN_ADDRESS:
			// ignore
			break;
		case TOKEN_CATEGORY:
			// ignore
			break;
		case TOKEN_SPLIT_CATEGORY:
			// ignore
			break;
		case TOKEN_SPLIT_AMOUNT:
			TransactionContext split = context.getSplitForAmount();
			split.amount = Double.parseDouble(line);
			break;
		case TOKEN_SPLIT_MEMO:
			split = context.getSplitForMemo();
			split.memo = line;
			break;
		case TOKEN_END:
			entryCount += context.processTransactions(file, listener);
			context.reset();
			break;
		default:
			throw new QIFSyntaxException("Expected " + TOKEN_TYPE + " at beginning of line");
		}
		return entryCount;
	}

	@Override
	public String getConverterName() {
		return "QIF Parser";
	}

	private class TransactionContext {
		public Date date;
		public Double amount;
		public String payee;
		public String memo;
		public List<TransactionContext> split = new ArrayList<TransactionContext>();
		private TransactionContext currentSplit;

		public TransactionContext getSplitForMemo() {
			if (currentSplit == null || !TextUtils.isEmpty(currentSplit.memo)) {
				currentSplit = new TransactionContext();
				split.add(currentSplit);
			}
			return currentSplit;
		}

		public TransactionContext getSplitForAmount() {
			if (currentSplit == null || !TextUtils.isEmpty(currentSplit.memo)) {
				currentSplit = new TransactionContext();
				split.add(currentSplit);
			}
			return currentSplit;
		}

		public int processTransactions(FileContent file, FileEntrySink listener) {
			if (split.isEmpty()) {
				listener.onEntry(file, new FileEntry(date, payee, memo, amount));
				return 1;
			} else {
				for (int i = 0; i < split.size(); i++) {
					listener.onEntry(file, new FileEntry(date, payee, split.get(i).memo, split.get(i).amount));
				}
				return split.size();
			}
		}

		public void reset() {
			date = null;
			amount = null;
			payee = null;
			memo = null;
			split = new ArrayList<TransactionContext>();
			currentSplit = null;
		}
	}
}
