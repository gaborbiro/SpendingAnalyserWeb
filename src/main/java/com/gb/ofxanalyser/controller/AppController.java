package com.gb.ofxanalyser.controller;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.gb.ofxanalyser.model.FileBucket;
import com.gb.ofxanalyser.model.Spending;
import com.gb.ofxanalyser.model.User;
import com.gb.ofxanalyser.model.UserDocument;
import com.gb.ofxanalyser.service.finance.FinanceService;
import com.gb.ofxanalyser.service.user.UserDocumentService;
import com.gb.ofxanalyser.service.user.UserService;
import com.gb.ofxanalyser.util.FileValidator;
import com.gb.ofxanalyser.util.UserValidator;

@Controller
@RequestMapping("/")
public class AppController {

	@Autowired
	UserService userService;

	@Autowired
	UserDocumentService userDocumentService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	FileValidator fileValidator;

	@Autowired
	UserValidator userValidator;

	@Autowired
	FinanceService financeService;

	@InitBinder("fileBucket")
	protected void initBinderFileBucket(WebDataBinder binder) {
		binder.setValidator(fileValidator);
	}

	@InitBinder("user")
	protected void initBinderUser(WebDataBinder binder) {
		binder.setValidator(userValidator);
	}

	/**
	 * This method will list all existing users.
	 */
	@RequestMapping(value = { "/", "/list" }, method = RequestMethod.GET)
	public String listUsers(ModelMap model) {
		List<User> users = userService.findAllUsers();
		model.addAttribute("users", users);
		return "userslist";
	}

	/**
	 * This method will provide the medium to add a new user.
	 */
	@RequestMapping(value = { "/newuser" }, method = RequestMethod.GET)
	public String newUser(ModelMap model) {
		User user = new User();
		model.addAttribute("user", user);
		model.addAttribute("edit", false);
		return "registration";
	}

	/**
	 * This method will be called on form submission, handling POST request for
	 * saving user in database. It also validates the user input
	 */
	@RequestMapping(value = { "/newuser" }, method = RequestMethod.POST)
	public String saveUser(@Valid User user, BindingResult result, ModelMap model) {

		if (result.hasErrors()) {
			return "registration";
		}

		/*
		 * Preferred way to achieve uniqueness of field [sso] should be
		 * implementing custom @Unique annotation and applying it on field [sso]
		 * of Model class [User].
		 * 
		 * Below mentioned peace of code [if block] is to demonstrate that you
		 * can fill custom errors outside the validation framework as well while
		 * still using internationalized messages.
		 * 
		 */
		if (!userService.isUserSSOUnique(user.getId(), user.getSsoId())) {
			FieldError ssoError = new FieldError("user", "ssoId", messageSource.getMessage("non.unique.ssoId",
					new String[] { user.getSsoId() }, Locale.getDefault()));
			result.addError(ssoError);
			return "registration";
		}

		userService.saveUser(user);

		model.addAttribute("user", user);
		model.addAttribute("success",
				"User " + user.getFirstName() + " " + user.getLastName() + " registered successfully");
		// return "success";
		return "registrationsuccess";
	}

	/**
	 * This method will provide the medium to update an existing user.
	 */
	@RequestMapping(value = { "/edit-user-{id}" }, method = RequestMethod.GET)
	public String editUser(@PathVariable int id, ModelMap model) {
		User user = userService.findById(id);
		model.addAttribute("user", user);
		model.addAttribute("edit", true);
		return "registration";
	}

	/**
	 * This method will be called on form submission, handling POST request for
	 * updating user in database. It also validates the user input
	 */
	@RequestMapping(value = { "/edit-user-{id}" }, method = RequestMethod.POST)
	public String updateUser(@Valid User user, BindingResult result, ModelMap model) {
		if (result.hasErrors()) {
			return "registration";
		}
		userService.updateUser(user);
		model.addAttribute("success",
				"User " + user.getFirstName() + " " + user.getLastName() + " updated successfully");
		return "registrationsuccess";
	}

	/**
	 * This method will delete an user by it's SSOID value.
	 */
	@RequestMapping(value = { "/delete-user-{id}" }, method = RequestMethod.GET)
	public String deleteUser(@PathVariable int id) {
		userService.deleteUserById(id);
		return "redirect:/list";
	}

	@RequestMapping(value = { "/add-document-{userId}" }, method = RequestMethod.GET)
	public String addDocuments(@PathVariable int userId, ModelMap model) {
		User user = userService.findById(userId);
		model.addAttribute("user", user);

		FileBucket fileModel = new FileBucket();
		model.addAttribute("fileBucket", fileModel);

		List<UserDocument> documents = userDocumentService.findAllByUserId(userId);
		model.addAttribute("documents", documents);

		FinanceService.Builder builder = financeService.builder(null);

		for (int i = 0; i < documents.size(); i++) {
			builder.with(documents.get(i).getName(), documents.get(i).getContent());
		}

		Spending[] spendings = builder.build().doAggregate();
		model.addAttribute("spendings", spendings);

		return "managedocuments";
	}

	@RequestMapping(value = { "/download-document-{userId}-{docId}" }, method = RequestMethod.GET)
	public String downloadDocument(@PathVariable int userId, @PathVariable int docId, HttpServletResponse response)
			throws IOException {
		UserDocument document = userDocumentService.findById(docId);
		response.setContentType(document.getContentType());
		response.setContentLength(document.getContent().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + document.getName() + "\"");

		FileCopyUtils.copy(document.getContent(), response.getOutputStream());

		return "redirect:/add-document-" + userId;
	}

	@RequestMapping(value = { "/delete-document-{userId}-{docId}" }, method = RequestMethod.GET)
	public String deleteDocument(@PathVariable int userId, @PathVariable int docId) {
		userDocumentService.deleteById(docId);
		return "redirect:/add-document-" + userId;
	}

	@RequestMapping(value = { "/add-document-{userId}" }, method = RequestMethod.POST)
	public String uploadDocument(@Valid FileBucket fileBucket, BindingResult result, ModelMap model,
			@PathVariable int userId) throws IOException {

		if (result.hasErrors()) {
			System.out.println("validation errors");
			User user = userService.findById(userId);
			model.addAttribute("user", user);

			List<UserDocument> documents = userDocumentService.findAllByUserId(userId);
			model.addAttribute("documents", documents);

			return "managedocuments";
		} else {
			System.out.println("Fetching file");

			User user = userService.findById(userId);
			model.addAttribute("user", user);

			saveDocument(fileBucket, user);

			return "redirect:/add-document-" + userId;
		}
	}

	private void saveDocument(FileBucket fileBucket, User user) throws IOException {

		UserDocument document = new UserDocument();

		MultipartFile multipartFile = fileBucket.getFile();

		document.setName(multipartFile.getOriginalFilename());
		document.setDescription(fileBucket.getDescription());
		document.setContentType(multipartFile.getContentType());
		document.setContent(multipartFile.getBytes());
		document.setUser(user);
		userDocumentService.saveDocument(document);
	}

}
