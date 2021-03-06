package com.gb.ofxanalyser.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gb.ofxanalyser.model.Translator;
import com.gb.ofxanalyser.model.fe.User;
import com.gb.ofxanalyser.service.UserService;

@Controller
@RequestMapping("/rest/v1")
public class JsonController {

	@Autowired
	UserService userService;

	@RequestMapping(value = { "/users" }, method = RequestMethod.GET)
	public @ResponseBody List<User> listUsersInJSON() {
		return Translator.get(userService.findAllUsers());
	}
}
