package com.gb.ofxanalyser.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.gb.ofxanalyser.model.be.UserBE;

@Repository("userDao")
public class UserDaoImpl extends AbstractDao<Integer, UserBE> implements UserDao {

	public UserBE findById(int id) {
		UserBE user = getByKey(id);
		return user;
	}

	public UserBE findByEmail(String email) {
		Criteria crit = createEntityCriteria();
		crit.add(Restrictions.eq("email", email));
		UserBE user = (UserBE) crit.uniqueResult();
		return user;
	}

	@SuppressWarnings("unchecked")
	public List<UserBE> findAllUsers() {
		Criteria criteria = createEntityCriteria().addOrder(Order.asc("firstName"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);// To avoid
																		// duplicates.
		List<UserBE> users = (List<UserBE>) criteria.list();
		return users;
	}

	public void save(UserBE user) {
		persist(user);
	}

	public void deleteById(int id) {
		Criteria crit = createEntityCriteria();
		crit.add(Restrictions.eq("id", id));
		UserBE user = (UserBE) crit.uniqueResult();
		delete(user);
	}
}
