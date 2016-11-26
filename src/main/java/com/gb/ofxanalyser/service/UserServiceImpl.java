package com.gb.ofxanalyser.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gb.ofxanalyser.dao.UserDao;
import com.gb.ofxanalyser.model.be.UserBE;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao dao;

	public UserBE findById(int id) {
		return dao.findById(id);
	}

	public UserBE findByEmail(String email) {
		return dao.findByEmail(email);
	}

	public void saveUser(UserBE user) {
		dao.save(user);
	}

	/*
	 * Since the method is running with TransactionBE, No need to call hibernate
	 * update explicitly. Just fetch the entity from db and update it with
	 * proper values within transaction. It will be updated in db once
	 * transaction ends.
	 */
	public void updateUser(UserBE user) {
		UserBE entity = dao.findById(user.getId());
		if (entity != null) {
			entity.setFirstName(user.getFirstName());
			entity.setLastName(user.getLastName());
			entity.setEmail(user.getEmail());
		}
	}

	public void deleteUserById(int id) {
		dao.deleteById(id);
	}

	public List<UserBE> findAllUsers() {
		return dao.findAllUsers();
	}

	public boolean isEmailUnique(Integer id, String email) {
		UserBE user = findByEmail(email);
		return (user == null || ((id != null) && (user.getId() == id)));
	}
}
