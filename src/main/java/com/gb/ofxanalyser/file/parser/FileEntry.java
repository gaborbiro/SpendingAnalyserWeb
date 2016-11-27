package com.gb.ofxanalyser.file.parser;

import java.text.DateFormat;
import java.util.Date;

import net.sf.ofx4j.domain.data.common.Transaction;

public class FileEntry {
	public String ID;
	public Date datePosted;
	public String payee;
	public String memo;
	public Double amount;

	public FileEntry(Transaction transaction) {
		this.ID = transaction.getId();
		this.datePosted = transaction.getDatePosted();
		this.payee = transaction.getName();
		this.memo = transaction.getMemo();
		this.amount = transaction.getAmount();
	}

	public FileEntry(Date datePosted, String payee, String memo, Double amount) {
		this.datePosted = datePosted;
		this.payee = payee;
		this.memo = memo;
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "FileEntry [datePosted=" + DateFormat.getDateTimeInstance().format(datePosted) + ", memo=" + memo
				+ ", payee=" + payee + ", amount=" + amount + ", ID=" + ID + "]";
	}
}