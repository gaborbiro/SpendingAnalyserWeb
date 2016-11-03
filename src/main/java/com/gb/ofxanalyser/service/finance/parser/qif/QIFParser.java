package com.gb.ofxanalyser.service.finance.parser.qif;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import com.gb.ofxanalyser.service.finance.parser.Document;
import com.gb.ofxanalyser.service.finance.parser.ParseException;
import com.gb.ofxanalyser.service.finance.parser.TransactionExtractor;
import com.gb.ofxanalyser.service.finance.parser.TransactionItem;
import com.gb.ofxanalyser.util.TextUtils;

public class QIFParser implements TransactionExtractor {

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
	public List<TransactionItem> getTransactions(Document file) throws ParseException {
		InputStream is = null;
		BufferedReader bfReader = null;
		List<TransactionItem> result = new ArrayList<TransactionItem>();
		try {
			is = new ByteArrayInputStream(file.getContent());
			bfReader = new BufferedReader(new InputStreamReader(is));
			TransactionContext context = new TransactionContext();
			String line = null;
			while ((line = bfReader.readLine()) != null) {
				result.addAll(processLine(context, line));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (QIFSyntaxException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception ex) {
			}
		}
		return result;
	}

	private List<TransactionItem> processLine(TransactionContext context, String line) throws QIFSyntaxException {
		List<TransactionItem> result = new ArrayList<TransactionItem>();
		char c = line.charAt(0);
		line = line.substring(1);
		switch (c) {
		case TOKEN_TYPE:

			break;
		case TOKEN_DATE:
			int year = Integer.parseInt(line.substring(6, 10));
			int month = Integer.parseInt(line.substring(3, 5)) - 1; // java month numbers are zero-based
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
			result.addAll(Arrays.asList(context.getTransactions()));
			context.reset();
			break;
		default:
			throw new QIFSyntaxException("Expected " + TOKEN_TYPE + " at beginning of line");
		}
		return result;
	}

	@Override
	public String getParserName() {
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

		public TransactionItem[] getTransactions() {
			if (split.isEmpty()) {
				return new TransactionItem[] { new TransactionItem(date, payee, memo, amount) };
			} else {
				TransactionItem[] result = new TransactionItem[split.size()];

				for (int i = 0; i < split.size(); i++) {
					result[i] = new TransactionItem(date, payee, split.get(i).memo, split.get(i).amount);
				}
				return result;
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
