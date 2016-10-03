package com.gb.ofxanalyser.service.finance.parser;

import java.util.List;

public interface TransactionExtractor {

	public List<TransactionItem> getTransactions() throws ParseException;
}
