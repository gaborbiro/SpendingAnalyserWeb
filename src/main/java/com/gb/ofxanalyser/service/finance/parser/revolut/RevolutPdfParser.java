package com.gb.ofxanalyser.service.finance.parser.revolut;

import java.util.List;

import com.gb.ofxanalyser.service.finance.parser.TransactionExtractor;
import com.gb.ofxanalyser.service.finance.parser.ParseException;
import com.gb.ofxanalyser.service.finance.parser.TransactionItem;

public class RevolutPdfParser implements TransactionExtractor {

	public List<TransactionItem> getTransactions() throws ParseException {
		throw new ParseException("Stub");
	}
}
