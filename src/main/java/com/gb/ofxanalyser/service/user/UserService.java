package com.gb.ofxanalyser.service.user;

import java.util.List;

import com.gb.ofxanalyser.model.User;


public interface UserService {
	
	User findById(int id);
	
	User findBySSO(String sso);
	
	void saveUser(User user);
	
	void updateUser(User user);
	
	void deleteUserById(int id);

	List<User> findAllUsers(); 
	
	boolean isUserSSOUnique(Integer id, String sso);

}