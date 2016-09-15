package com.gb.ofxanalyser.service.finance.parser;

import java.util.Map;

public interface FileParser {

	public void parse(byte[] file, Map<TransactionItem, TransactionAggregate> transactionAggregate);
}
