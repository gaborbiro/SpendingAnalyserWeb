package com.gb.ofxanalyser.service.user;

import java.util.List;

import com.gb.ofxanalyser.model.be.UserBE;

public interface UserService {

	UserBE findById(int id);

	UserBE findByEmail(String email);

	void saveUser(UserBE user);

	void updateUser(UserBE user);

	void deleteUserById(int id);

	List<UserBE> findAllUsers();

	boolean isEmailUnique(Integer id, String email);
}