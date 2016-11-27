package com.gb.ofxanalyser.dao;

import java.util.List;

import org.hibernate.criterion.Order;

import com.gb.ofxanalyser.model.be.TransactionBE;

public interface TransactionDao {

	List<TransactionBE> findAll();

	List<TransactionBE> findAllByUserId(int userId, Order... orders);

	List<TransactionBE> findAllSubscriptionsByUserId(int userId, Order... orders);

	void save(TransactionBE transaction);

	void update(List<TransactionBE> transaction);

	void deleteById(int id);
}
