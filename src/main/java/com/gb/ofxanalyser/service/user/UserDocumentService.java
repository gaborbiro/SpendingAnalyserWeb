package com.gb.ofxanalyser.service.user;

import java.util.List;

import com.gb.ofxanalyser.model.be.UserDocumentBE;

public interface UserDocumentService {

	UserDocumentBE findById(int id);

	List<UserDocumentBE> findAllByUserId(int id);
	
	void saveDocument(UserDocumentBE document);
	
	void deleteById(int id);
}
