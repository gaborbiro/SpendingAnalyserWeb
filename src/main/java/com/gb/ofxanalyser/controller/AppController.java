package com.gb.ofxanalyser.controller;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.gb.ofxanalyser.model.Translator;
import com.gb.ofxanalyser.model.be.UserBE;
import com.gb.ofxanalyser.model.be.UserDocumentBE;
import com.gb.ofxanalyser.model.fe.FileBucket;
import com.gb.ofxanalyser.model.fe.HistorySorting;
import com.gb.ofxanalyser.model.fe.TransactionFE;
import com.gb.ofxanalyser.model.fe.User;
import com.gb.ofxanalyser.service.CategorisationService;
import com.gb.ofxanalyser.service.TransactionService;
import com.gb.ofxanalyser.service.UserDocumentService;
import com.gb.ofxanalyser.service.UserService;
import com.gb.ofxanalyser.util.TextUtils;

@Controller
@RequestMapping("/")
@SessionAttributes({ "sorting", "subscriptionsSorting" })
public class AppController {

	@Autowired
	UserService userService;

	@Autowired
	UserDocumentService userDocumentService;

	@Autowired
	TransactionService transactionService;

	@Autowired
	CategorisationService categorisationService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	FileValidator fileValidator;

	@Autowired
	UserValidator userValidator;

	@InitBinder("fileBucket")
	protected void initBinderFileBucket(WebDataBinder binder) {
		binder.addValidators(fileValidator);
	}

	@InitBinder("user")
	protected void initBinderUser(WebDataBinder binder) {
		binder.addValidators(userValidator);
	}

	/**
	 * This method will list all existing users.
	 */
	@RequestMapping(value = { "/", "/list" }, method = RequestMethod.GET)
	public String listUsers(ModelMap model) {
		model.addAttribute("users", Translator.get(userService.findAllUsers()));
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
		 * Preferred way to achieve uniqueness of field [email] should be
		 * implementing custom @Unique annotation and applying it on field
		 * [email] of Model class [UserBE].
		 * 
		 * Below mentioned peace of code [if block] is to demonstrate that you
		 * can fill custom errors outside the validation framework as well while
		 * still using internationalized messages.
		 * 
		 */
		if (!userService.isEmailUnique(user.getId(), user.getEmail())) {
			FieldError emailError = new FieldError("user", "email", messageSource.getMessage("non.unique.email",
					new String[] { user.getEmail() }, Locale.getDefault()));
			result.addError(emailError);
			return "registration";
		}

		UserBE userBE = Translator.get(user);
		userService.saveUser(userBE);
		model.addAttribute("user", Translator.get(userBE));

		model.addAttribute("success",
				"User " + user.getFirstName() + " " + user.getLastName() + " registered successfully");
		return "registrationsuccess";
	}

	/**
	 * This method will provide the medium to update an existing user.
	 */
	@RequestMapping(value = { "/edit-user-{id}" }, method = RequestMethod.GET)
	public String editUser(@PathVariable int id, ModelMap model) {
		UserBE user = userService.findById(id);
		model.addAttribute("user", Translator.get(user));
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
		userService.updateUser(Translator.get(user));
		model.addAttribute("success",
				"User " + user.getFirstName() + " " + user.getLastName() + " updated successfully");
		return "registrationsuccess";
	}

	/**
	 * This method will delete an user by it's id.
	 */
	@RequestMapping(value = { "/delete-user-{id}" }, method = RequestMethod.GET)
	public String deleteUser(@PathVariable int id) {
		userService.deleteUserById(id);
		return "redirect:/list";
	}

	private static final String SORT_NAME_MEMO = "memoname";
	private static final String SORT_CATEGORY = "category";
	private static final String SORT_IS_SUBSCRIPTION = "subscription";
	private static final String SORT_DATE = "date";
	private static final String SORT_AMOUNT = "amount";

	@RequestMapping(value = { "/add-document-{userId}" }, method = RequestMethod.GET)
	public String addDocuments(@PathVariable int userId, ModelMap model,
			@RequestParam(required = false) String togglesort) {
		UserBE user = userService.findById(userId);

		if (user == null) {
			return "redirect:/list";
		}

		model.addAttribute("user", Translator.get(user));

		FileBucket fileModel = new FileBucket();
		model.addAttribute("fileBucket", fileModel);

		List<UserDocumentBE> documents = userDocumentService.findAllByUserId(userId);
		model.addAttribute("documents", Translator.get(documents));

		HistorySorting sorting = null;

		if (model.containsKey("sorting")) {
			sorting = (HistorySorting) model.get("sorting");
		} else {
			sorting = HistorySorting.getSortingByDate();
			model.addAttribute("sorting", sorting);
		}

		if (togglesort != null) {
			switch (togglesort) {
			case SORT_NAME_MEMO:
				sorting.toggle(HistorySorting.CRIT_MEM_ASC, true);
				break;
			case SORT_CATEGORY:
				sorting.toggle(HistorySorting.CRIT_CAT_ASC, true);
				break;
			case SORT_IS_SUBSCRIPTION:
				sorting.toggle(HistorySorting.CRIT_SUB_ASC, true);
				break;
			case SORT_DATE:
				sorting.toggle(HistorySorting.CRIT_DAT_ASC, true);
				break;
			case SORT_AMOUNT:
				sorting.toggle(HistorySorting.CRIT_VAL_ASC, true);
				break;
			default:
				break;
			}
		}

		List<TransactionFE> transactions = Translator.get(transactionService.findAllByUserId(userId, false, sorting));

		model.addAttribute("transactions", transactions);

		return "managedocuments";
	}

	@RequestMapping(value = { "/delete-document-{userId}-{docId}" }, method = RequestMethod.GET)
	public String deleteDocument(@PathVariable int userId, @PathVariable int docId) {
		userDocumentService.deleteById(docId);
		return "redirect:/add-document-" + userId;
	}

	@RequestMapping(value = { "/add-document-{userId}" }, method = RequestMethod.POST)
	public String uploadDocument(@Valid FileBucket fileBucket, BindingResult result, ModelMap model,
			@PathVariable int userId) throws IOException {
		UserBE user = userService.findById(userId);
		model.addAttribute("user", Translator.get(user));

		if (result.hasErrors()) {
			List<UserDocumentBE> documents = userDocumentService.findAllByUserId(userId);
			model.addAttribute("documents", Translator.get(documents));
		} else {
			DocumentsHandler documentsHandler = new DocumentsHandler(userDocumentService, transactionService,
					categorisationService);
			String warning = documentsHandler.processDocuments(user, fileBucket);

			addDocuments(userId, model, null);

			if (!TextUtils.isEmpty(warning)) {
				model.addAttribute("fileReject", warning);
			}
		}
		return "managedocuments";
	}

	/**
	 * This method will provide the medium to update an existing user.
	 */
	@RequestMapping(value = { "/stats-{id}" }, method = RequestMethod.GET)
	public String stats(@PathVariable int id, ModelMap model) {
		UserBE user = userService.findById(id);
		model.addAttribute("user", Translator.get(user));

		List<TransactionFE> transactions = Translator.get(transactionService.findAllByUserId(id, false, null));
		model.addAttribute("transactions", transactions);
		return "stats";
	}

	/**
	 * This method will provide the medium to update an existing user.
	 */
	@RequestMapping(value = { "/subscriptions-{userId}" }, method = RequestMethod.GET)
	public String subscriptions(@PathVariable int userId, ModelMap model,
			@RequestParam(required = false) String togglesort) {
		UserBE user = userService.findById(userId);

		if (user == null) {
			return "redirect:/list";
		}

		model.addAttribute("user", Translator.get(user));

		HistorySorting subscriptionSorting = null;

		if (model.containsKey("subscriptionsSorting")) {
			subscriptionSorting = (HistorySorting) model.get("subscriptionsSorting");
		} else {
			subscriptionSorting = HistorySorting.getSortingByDate();
			model.addAttribute("subscriptionsSorting", subscriptionSorting);
		}

		if (togglesort != null) {
			switch (togglesort) {
			case SORT_NAME_MEMO:
				subscriptionSorting.toggle(HistorySorting.CRIT_MEM_ASC, true);
				break;
			case SORT_CATEGORY:
				subscriptionSorting.toggle(HistorySorting.CRIT_CAT_ASC, true);
				break;
			case SORT_DATE:
				subscriptionSorting.toggle(HistorySorting.CRIT_DAT_ASC, true);
				break;
			case SORT_AMOUNT:
				subscriptionSorting.toggle(HistorySorting.CRIT_VAL_ASC, true);
				break;
			default:
				break;
			}
		}

		List<TransactionFE> transactions = Translator
				.get(transactionService.findAllByUserId(userId, true, subscriptionSorting));

		model.addAttribute("transactions", transactions);

		return "subscriptions";
	}
}
