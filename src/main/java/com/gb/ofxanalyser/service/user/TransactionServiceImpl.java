package com.gb.ofxanalyser.service.user;

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

	public List<TransactionBE> findAllByUserId(int userId, HistorySorting sorting) {
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
					orders.add(Order.asc(TransactionBE.IS_SUBSCRIPTION));
					break;
				case HistorySorting.CRIT_SUB_DSC:
					orders.add(Order.desc(TransactionBE.IS_SUBSCRIPTION));
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
		return dao.findAllByUserId(userId, orders.toArray(new Order[orders.size()]));
	}

	@Override
	public void saveTransaction(TransactionBE transaction) {
		dao.save(transaction);
	}
}
