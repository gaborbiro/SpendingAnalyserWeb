package com.gb.ofxanalyser.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
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
import com.gb.ofxanalyser.model.fe.UserFE;
import com.gb.ofxanalyser.model.fe.base.Sorting;
import com.gb.ofxanalyser.service.categories.CategorisationService;
import com.gb.ofxanalyser.service.finance.TransactionsService;
import com.gb.ofxanalyser.service.user.TransactionService;
import com.gb.ofxanalyser.service.user.UserDocumentService;
import com.gb.ofxanalyser.service.user.UserService;
import com.gb.ofxanalyser.util.FileValidator;
import com.gb.ofxanalyser.util.UserValidator;

@Controller
@RequestMapping("/")
@SessionAttributes("sorting")
public class AppController {

	@Autowired
	UserService userService;

	@Autowired
	UserDocumentService userDocumentService;

	@Autowired
	TransactionService transactionService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	FileValidator fileValidator;

	@Autowired
	UserValidator userValidator;

	@Autowired
	TransactionsService transactionsService;

	@Autowired
	CategorisationService categorisationService;

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
		model.addAttribute("users", Translator.get(userService.findAllUsers()));
		return "userslist";
	}

	/**
	 * This method will provide the medium to add a new user.
	 */
	@RequestMapping(value = { "/newuser" }, method = RequestMethod.GET)
	public String newUser(ModelMap model) {
		UserFE user = new UserFE();
		model.addAttribute("user", user);
		model.addAttribute("edit", false);
		return "registration";
	}

	/**
	 * This method will be called on form submission, handling POST request for
	 * saving user in database. It also validates the user input
	 */
	@RequestMapping(value = { "/newuser" }, method = RequestMethod.POST)
	public String saveUser(@Valid UserFE user, BindingResult result, ModelMap model) {
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
		// return "success";
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
	public String updateUser(@Valid UserFE user, BindingResult result, ModelMap model) {
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

		// // Adding attributes that are part of identity, while respecting the
		// // users sorting order. We don't want Transactions with same name to
		// // override each other just because the user is sorting only by name
		// HistorySorting finalSorting = new HistorySorting(sorting);
		// if (finalSorting.isSet(HistorySorting.CRIT_VAL_ASC) == 0) {
		// finalSorting.toggle(HistorySorting.CRIT_VAL_ASC, false);
		// }
		//
		// if (finalSorting.isSet(HistorySorting.CRIT_DAT_ASC) == 0) {
		// finalSorting.toggle(HistorySorting.CRIT_DAT_ASC, false);
		// }
		//
		// if (finalSorting.isSet(HistorySorting.CRIT_MEM_ASC) == 0) {
		// finalSorting.toggle(HistorySorting.CRIT_MEM_ASC, false);
		// }

		List<TransactionFE> transactions = Translator.get(transactionService.findAllByUserId(userId, sorting));

		for (TransactionFE transaction : transactions) {
			transaction.setCategory(categorisationService.getCategoryForTransaction(transaction.getDescription()));
			transaction.setSubscription(categorisationService.isSubscription(transaction.getDescription()));
		}

		Comparator<TransactionFE> comparator = null;

		if (sorting.getCount() > 0) {
			switch (sorting.get(0)) {
			case HistorySorting.CRIT_CAT_ASC:
				comparator = new Comparator<TransactionFE>() {

					@Override
					public int compare(TransactionFE transaction1, TransactionFE transaction2) {
						return Sorting.compare(transaction1.getCategory(), transaction2.getCategory());
					}
				};
				break;
			case HistorySorting.CRIT_CAT_DSC:
				comparator = new Comparator<TransactionFE>() {

					@Override
					public int compare(TransactionFE transaction1, TransactionFE transaction2) {
						return Sorting.compare(transaction2.getCategory(), transaction1.getCategory());
					}
				};
				break;
			case HistorySorting.CRIT_SUB_ASC:
				comparator = new Comparator<TransactionFE>() {

					@Override
					public int compare(TransactionFE transaction1, TransactionFE transaction2) {
						return Sorting.compare(transaction1.isSubscription(), transaction2.isSubscription());
					}
				};
				break;
			case HistorySorting.CRIT_SUB_DSC:
				comparator = new Comparator<TransactionFE>() {

					@Override
					public int compare(TransactionFE transaction1, TransactionFE transaction2) {
						return Sorting.compare(transaction2.isSubscription(), transaction2.isSubscription());
					}
				};
				break;
			}
		}
		if (comparator != null) {
			Collections.sort(transactions, comparator);
		}

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
			return "managedocuments";
		} else {
			transactionsService.processDocuments(user, fileBucket);
			return "redirect:/add-document-" + userId;
		}
	}

	/**
	 * This method will provide the medium to update an existing user.
	 */
	@RequestMapping(value = { "/stats-{id}" }, method = RequestMethod.GET)
	public String stats(@PathVariable int id, ModelMap model) {
		UserBE user = userService.findById(id);
		model.addAttribute("user", Translator.get(user));

		List<TransactionFE> transactions = Translator.get(transactionService.findAllByUserId(id, null));
		model.addAttribute("transactions", transactions);
		return "stats";
	}
}
