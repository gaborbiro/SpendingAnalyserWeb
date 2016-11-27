package com.gb.ofxanalyser.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gb.ofxanalyser.dao.TransactionDao;
import com.gb.ofxanalyser.model.be.TransactionBE;
import com.gb.ofxanalyser.model.fe.HistorySorting;

@Service("transactionService")
@Transactional
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	TransactionDao dao;

	@Autowired
	CategorisationService categorisationService;

	public List<TransactionBE> findAllByUserId(int userId, boolean subscriptionsOnly, HistorySorting sorting) {
		if (categorisationService.hasMappingFileChanged()) {
			updateTransactionCategories();
		}

		List<Order> orders = new ArrayList<>();

		if (sorting != null) {
			for (int i = 0; i < sorting.getCount(); i++) {
				switch (sorting.get(i)) {
				case HistorySorting.CRIT_CAT_ASC:
					orders.add(Order.asc(TransactionBE.CATEGORY));
					break;
				case HistorySorting.CRIT_CAT_DSC:
					orders.add(Order.desc(TransactionBE.CATEGORY));
					break;
				case HistorySorting.CRIT_DAT_ASC:
					orders.add(Order.asc(TransactionBE.DATE));
					break;
				case HistorySorting.CRIT_DAT_DSC:
					orders.add(Order.desc(TransactionBE.DATE));
					break;
				case HistorySorting.CRIT_MEM_ASC:
					orders.add(Order.asc(TransactionBE.DESCRIPTION));
					break;
				case HistorySorting.CRIT_MEM_DSC:
					orders.add(Order.desc(TransactionBE.DESCRIPTION));
					break;
				case HistorySorting.CRIT_SUB_ASC:
					orders.add(Order.asc("isSubscription"));
					break;
				case HistorySorting.CRIT_SUB_DSC:
					orders.add(Order.desc("isSubscription"));
					break;
				case HistorySorting.CRIT_VAL_ASC:
					orders.add(Order.asc(TransactionBE.AMOUNT));
					break;
				case HistorySorting.CRIT_VAL_DSC:
					orders.add(Order.desc(TransactionBE.AMOUNT));
					break;
				}
			}
		}
		if (subscriptionsOnly) {
			return dao.findAllSubscriptionsByUserId(userId, orders.toArray(new Order[orders.size()]));
		} else {
			return dao.findAllByUserId(userId, orders.toArray(new Order[orders.size()]));
		}
	}

	@Override
	public void saveTransaction(TransactionBE transaction) {
		dao.save(transaction);
	}

	private void updateTransactionCategories() {
		List<TransactionBE> transactions = dao.findAll();

		for (TransactionBE transaction : transactions) {
			transaction.setCategory(categorisationService.getCategoryForTransaction(transaction.getDescription()));
			transaction.setIsSubscription(
					(byte) (categorisationService.isSubscription(transaction.getDescription()) ? 1 : 0));
		}
		dao.update(transactions);
		categorisationService.markMappingFileAsAccessed();
	}
}
