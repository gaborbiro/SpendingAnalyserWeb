package com.gb.ofxanalyser.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gb.ofxanalyser.model.be.TransactionBE;
import com.gb.ofxanalyser.model.be.UserBE;
import com.gb.ofxanalyser.model.be.UserDocumentBE;
import com.gb.ofxanalyser.model.fe.TransactionFE;
import com.gb.ofxanalyser.model.fe.UserDocumentFE;
import com.gb.ofxanalyser.util.TextUtils;
import com.gb.ofxanalyser.model.fe.User;

@SuppressWarnings("rawtypes")
public class Translator {

	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MMM/yy");
	private static NumberFormat DECIMAL_FORMAT = DecimalFormat.getCurrencyInstance();

	private static String CATEGORY_UNKNOWN = "Unknown";

	@SuppressWarnings("unchecked")
	public static <R> List<R> get(List list) {
		if (list == null) {
			return null;
		}
		if (list.isEmpty()) {
			return new ArrayList<R>();
		}
		try {
			Method getter = Translator.class.getDeclaredMethod("get", list.toArray()[0].getClass());
			List<R> result = new ArrayList<>();
			for (Object item : list) {
				result.add((R) getter.invoke(null, item));
			}
			return result;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public static TransactionFE get(TransactionBE transactionBE) {
		TransactionFE transactionFE = new TransactionFE();
		transactionFE.setDescription(transactionBE.getDescription());
		transactionFE.setDate(DATE_FORMAT.format(new Date(transactionBE.getDate().getTime())));
		transactionFE.setAmount(DECIMAL_FORMAT.format(transactionBE.getAmount() / 100));
		String category = transactionBE.getCategory();
		transactionFE.setCategory(TextUtils.isEmpty(category) ? CATEGORY_UNKNOWN : category);
		transactionFE.setSubscription(transactionBE.getIsSubscription() == 1 ? true : false);
		return transactionFE;
	}

	public static UserDocumentFE get(UserDocumentBE userDocumentBE) {
		UserDocumentFE userDocumentFE = new UserDocumentFE();
		userDocumentFE.setId(userDocumentBE.getId());
		userDocumentFE.setName(userDocumentBE.getName());
		userDocumentFE.setDescription(userDocumentBE.getDescription());
		userDocumentFE.setContentType(userDocumentBE.getContentType());

		StringBuffer buffer = new StringBuffer();
		buffer.append(DATE_FORMAT.format(new Date(userDocumentBE.getStartDate())));
		buffer.append("-");
		buffer.append(DATE_FORMAT.format(new Date(userDocumentBE.getEndDate())));
		userDocumentFE.setPeriod(buffer.toString());
		return userDocumentFE;
	}

	public static User get(UserBE userBE) {
		User userFE = new User();
		userFE.setId(userBE.getId());
		userFE.setFirstName(userBE.getFirstName());
		userFE.setLastName(userBE.getLastName());
		userFE.setEmail(userBE.getEmail());
		return userFE;
	}

	public static UserBE get(User userFE) {
		UserBE userBE = new UserBE();
		userBE.setId(userFE.getId());
		userBE.setFirstName(userFE.getFirstName());
		userBE.setLastName(userFE.getLastName());
		userBE.setEmail(userFE.getEmail());
		return userBE;
	}
}
