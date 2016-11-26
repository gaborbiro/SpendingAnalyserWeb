package com.gb.ofxanalyser.service;

import java.util.List;

import com.gb.ofxanalyser.model.be.TransactionBE;
import com.gb.ofxanalyser.model.fe.HistorySorting;

public interface TransactionService {

	public List<TransactionBE> findAllByUserId(int userId, boolean subscriptionsOnly, HistorySorting sorting);

	void saveTransaction(TransactionBE transaction);
}
