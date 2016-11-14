package com.gb.ofxanalyser.dao;

import java.util.List;

import com.gb.ofxanalyser.model.be.UserDocumentBE;

public interface UserDocumentDao {

	List<UserDocumentBE> findAll();
	
	UserDocumentBE findById(int id);
	
	void save(UserDocumentBE document);
	
	List<UserDocumentBE> findAllByUserId(int userId);
	
	void deleteById(int id);
}
