package com.gb.ofxanalyser.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gb.ofxanalyser.dao.UserDocumentDao;
import com.gb.ofxanalyser.model.be.UserDocumentBE;

@Service("userDocumentService")
@Transactional
public class UserDocumentServiceImpl implements UserDocumentService {

	@Autowired
	UserDocumentDao dao;

	public UserDocumentBE findById(int id) {
		return dao.findById(id);
	}

	public List<UserDocumentBE> findAllByUserId(int userId) {
		return dao.findAllByUserId(userId);
	}

	public void saveDocument(UserDocumentBE document) {
		dao.save(document);
	}

	public void deleteById(int id) {
		dao.deleteById(id);
	}

}
