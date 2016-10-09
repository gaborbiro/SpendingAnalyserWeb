package com.gb.ofxanalyser.service.finance.parser;

import java.text.DateFormat;
import java.util.Date;

import net.sf.ofx4j.domain.data.common.Transaction;

public class TransactionItem {
	public String ID;
	public Date datePosted;
	public String payee;
	public String memo;
	public Double amount;

	public TransactionItem(String ID, Date datePosted, String name, String memo, Double amount) {
		this.ID = ID;
		this.datePosted = datePosted;
		this.payee = name;
		this.memo = memo;
		this.amount = amount;
	}

	public TransactionItem(Transaction transaction) {
		this.ID = transaction.getId();
		this.datePosted = transaction.getDatePosted();
		this.payee = transaction.getName();
		this.memo = transaction.getMemo();
		this.amount = transaction.getAmount();
	}

	public TransactionItem(Date datePosted, String payee, String memo, Double amount) {
		this.datePosted = datePosted;
		this.payee = payee;
		this.memo = memo;
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "TransactionItem [datePosted=" + DateFormat.getDateTimeInstance().format(datePosted) + ", memo=" + memo
				+ ", payee=" + payee + ", amount=" + amount + ", ID=" + ID + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ID == null) ? 0 : ID.hashCode());
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((datePosted == null) ? 0 : datePosted.hashCode());
		result = prime * result + ((memo == null) ? 0 : memo.hashCode());
		result = prime * result + ((payee == null) ? 0 : payee.hashCode());
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
		if (ID == null) {
			if (other.ID != null)
				return false;
		} else if (!ID.equals(other.ID))
			return false;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (datePosted == null) {
			if (other.datePosted != null)
				return false;
		} else if (!datePosted.equals(other.datePosted))
			return false;
		if (memo == null) {
			if (other.memo != null)
				return false;
		} else if (!memo.equals(other.memo))
			return false;
		if (payee == null) {
			if (other.payee != null)
				return false;
		} else if (!payee.equals(other.payee))
			return false;
		return true;
	}
}