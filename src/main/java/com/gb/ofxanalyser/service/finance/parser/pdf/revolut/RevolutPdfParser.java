package com.gb.ofxanalyser.service.finance.parser.pdf.revolut;

import java.util.Map;

import com.gb.ofxanalyser.service.finance.parser.FileParser;
import com.gb.ofxanalyser.service.finance.parser.ParseException;
import com.gb.ofxanalyser.service.finance.parser.TransactionAggregate;
import com.gb.ofxanalyser.service.finance.parser.TransactionItem;

public class RevolutPdfParser implements FileParser {

	public void parse(byte[] file, Map<TransactionItem, TransactionAggregate> transactionAggregate)
			throws ParseException {
		throw new ParseException("Stub");
	}
}
