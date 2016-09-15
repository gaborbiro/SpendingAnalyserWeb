package com.gb.ofxanalyser.service.finance.parser;

import java.text.DateFormat;
import java.util.Date;

import net.sf.ofx4j.domain.data.common.Transaction;

public class TransactionItem {
	public String ID;
	public Date datePosted;
	public String name;
	public String memo;
	public Double amount;

	public TransactionItem(String ID, Date datePosted, String name, String memo, Double amount) {
		this.ID = ID;
		this.datePosted = datePosted;
		this.name = name;
		this.memo = memo;
		this.amount = amount;
	}

	public TransactionItem(Transaction transaction) {
		this.ID = transaction.getId();
		this.datePosted = transaction.getDatePosted();
		this.name = transaction.getName();
		this.memo = transaction.getMemo();
		this.amount = transaction.getAmount();
	}

	@Override
	public String toString() {
		return "TransactionItem [datePosted=" + DateFormat.getDateTimeInstance().format(datePosted) + ", memo=" + memo
				+ ", name=" + name + ", amount=" + amount + ", ID=" + ID + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((memo == null) ? 0 : memo.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransactionItem other = (TransactionItem) obj;
		if (memo == null) {
			if (other.memo != null)
				return false;
		} else if (!memo.equals(other.memo))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}