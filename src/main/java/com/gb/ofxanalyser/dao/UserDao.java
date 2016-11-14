package com.gb.ofxanalyser.dao;

import java.util.List;

import com.gb.ofxanalyser.model.be.UserBE;


public interface UserDao {

	UserBE findById(int id);
	
	UserBE findByEmail(String email);
	
	void save(UserBE user);
	
	void deleteById(int id);
	
	List<UserBE> findAllUsers();

}

