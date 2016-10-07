package com.gb.ofxanalyser.service.user;

import java.util.List;

import com.gb.ofxanalyser.model.User;

public interface UserService {

	User findById(int id);

	User findByEmail(String email);

	void saveUser(User user);

	void updateUser(User user);

	void deleteUserById(int id);

	List<User> findAllUsers();

	boolean isEmailUnique(Integer id, String email);

}