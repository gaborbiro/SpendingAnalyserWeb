package com.gb.ofxanalyser.service.finance.parser;

import java.util.ArrayList;
import java.util.List;

public class TransactionAggregate implements Comparable<TransactionAggregate> {
	public List<TransactionItem> transactions = new ArrayList<TransactionItem>();
	public double total;

	public int compareTo(TransactionAggregate o) {
		return (int) (total - o.total);
	}
}
