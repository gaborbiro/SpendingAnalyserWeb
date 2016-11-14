package com.gb.ofxanalyser.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.gb.ofxanalyser.model.be.UserDocumentBE;

@Repository("userDocumentDao")
public class UserDocumentDaoImpl extends AbstractDao<Integer, UserDocumentBE> implements UserDocumentDao {

	@SuppressWarnings("unchecked")
	public List<UserDocumentBE> findAll() {
		Criteria crit = createEntityCriteria();
		return (List<UserDocumentBE>) crit.list();
	}

	public void save(UserDocumentBE document) {
		persist(document);
	}

	public UserDocumentBE findById(int id) {
		return getByKey(id);
	}

	@SuppressWarnings("unchecked")
	public List<UserDocumentBE> findAllByUserId(int userId) {
		Criteria crit = createEntityCriteria();
		Criteria userCriteria = crit.createCriteria("user");
		userCriteria.add(Restrictions.eq("id", userId));
		return (List<UserDocumentBE>) crit.list();
	}

	public void deleteById(int id) {
		UserDocumentBE document = getByKey(id);
		delete(document);
	}

}
