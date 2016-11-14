package com.gb.ofxanalyser.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.gb.ofxanalyser.model.be.TransactionBE;

@Repository("transactionDao")
public class TransactionDaoImpl extends AbstractDao<Integer, TransactionBE> implements TransactionDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<TransactionBE> findAllByUserId(int userId, Order... orders) {
		Criteria crit = createEntityCriteria();
		Criteria userCriteria = crit.createCriteria("user");
		userCriteria.add(Restrictions.eq("id", userId));

		for (Order order : orders) {
			crit.addOrder(order);
		}
		return (List<TransactionBE>) crit.list();
	}

	@Override
	public void save(TransactionBE transaction) {
		persist(transaction);
	}

	@Override
	public void deleteById(int id) {
		Criteria crit = createEntityCriteria();
		crit.add(Restrictions.eq("id", id));
		TransactionBE transaction = (TransactionBE) crit.uniqueResult();
		delete(transaction);
	}
}
