package com.gb.ofxanalyser.service.finance.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransactionAggregate {
	public List<TransactionItem> transactions = new ArrayList<TransactionItem>();
	public double total;

	public TransactionAggregate() {
	}

	public TransactionAggregate(double total, TransactionItem... transactions) {
		this.total = total;
		this.transactions.addAll(Arrays.asList(transactions));
	}

	@Override
	public String toString() {
		return "TransactionAggregate [transactions=" + transactions + "]";
	}

}
