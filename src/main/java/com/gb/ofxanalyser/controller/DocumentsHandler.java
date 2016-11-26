package com.gb.ofxanalyser.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.web.multipart.MultipartFile;

import com.gb.ofxanalyser.model.be.TransactionBE;
import com.gb.ofxanalyser.model.be.UserBE;
import com.gb.ofxanalyser.model.be.UserDocumentBE;
import com.gb.ofxanalyser.model.fe.FileBucket;
import com.gb.ofxanalyser.service.CategorisationService;
import com.gb.ofxanalyser.service.TransactionService;
import com.gb.ofxanalyser.service.UserDocumentService;
import com.gb.ofxanalyser.service.file.FileParserService;
import com.gb.ofxanalyser.service.file.parser.FileContent;
import com.gb.ofxanalyser.service.file.parser.FileEntry;
import com.gb.ofxanalyser.service.file.parser.FileParser;
import com.gb.ofxanalyser.util.TextUtils;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class DocumentsHandler {

	UserDocumentService userDocumentService;
	TransactionService transactionService;
	CategorisationService categorisationService;

	public DocumentsHandler(UserDocumentService userDocumentService, TransactionService transactionService,
			CategorisationService categorisationService) {
		this.userDocumentService = userDocumentService;
		this.transactionService = transactionService;
		this.categorisationService = categorisationService;
	}

	public String processDocuments(UserBE user, FileBucket fileBucket) throws IOException {
		UserDocumentBE document = null;
		MultipartFile[] multipartFiles = fileBucket.getFiles();
		StringBuffer buffer = new StringBuffer();

		for (MultipartFile multipartFile : multipartFiles) {
			int transactionCount = 0;
			document = new UserDocumentBE();
			document.setName(multipartFile.getOriginalFilename());
			document.setDescription(fileBucket.getDescription());
			document.setContentType(multipartFile.getContentType());
			document.setUser(user);
			userDocumentService.saveDocument(document);

			Set<TransactionBE> transactions = processDocument(user, document, multipartFile);

			for (TransactionBE transaction : transactions) {
				try {
					transactionService.saveTransaction(transaction);
					transactionCount++;
				} catch (ConstraintViolationException e) {
					// ignoring duplicates
					if (e.getSQLException() instanceof MySQLIntegrityConstraintViolationException) {
						if (e.getSQLException().getMessage().contains("Duplicate")) {
							System.out.println("Duplicate: " + transaction.getDescription());
						} else {
							throw e;
						}
					}
				}
			}

			if (transactionCount == 0) {
				userDocumentService.deleteById(document.getId());
				if (buffer.length() == 0) {
					buffer.append("No new transactions found in: ");
				}
				buffer.append("<br>");
				buffer.append(multipartFile.getOriginalFilename());
			} else {
				userDocumentService.saveDocument(document);
			}
		}
		return buffer.toString();
	}

	/**
	 * The field UserDocumentBE.startDate/endDate will be updated
	 * 
	 * @throws IOException
	 */
	private Set<TransactionBE> processDocument(UserBE user, UserDocumentBE document, MultipartFile file)
			throws IOException {
		FileParserService.Builder builder = FileParserService.builder();
		builder.with(file);

		TransactionsSink transactionsSink = new TransactionsSink(user, document);
		builder.sink(transactionsSink);

		PeriodsSink periodsSink = new PeriodsSink();
		builder.sink(periodsSink);

		builder.build().process();

		Map<String, Long[]> periods = periodsSink.getPeriods();

		Long[] period = periods.get(file.getOriginalFilename());

		if (period[0] > 0 && period[1] > 0 && period[1] >= period[0]) {
			document.setStartDate(period[0]);
			document.setEndDate(period[1]);
		}
		return transactionsSink.getTransactions();
	}

	private class TransactionsSink implements FileParser.FileEntrySink {

		private Set<TransactionBE> transactions;
		private UserBE user;
		private UserDocumentBE document;

		public TransactionsSink(UserBE user, UserDocumentBE document) {
			this.user = user;
			this.document = document;
			transactions = new LinkedHashSet<>();
		}

		@Override
		public void onEntry(FileContent file, FileEntry entry) {
			StringBuffer buffer = new StringBuffer();
			buffer.append(entry.payee);
			if (!TextUtils.isEmpty(entry.memo)) {
				buffer.append("<br>");
				buffer.append(entry.memo);
			}

			String nameMemo = buffer.toString();

			TransactionBE transaction = new TransactionBE();
			transaction.setDescription(nameMemo);
			transaction.setDate(new Date(entry.datePosted.getTime()));
			transaction.setAmount(entry.amount * 100);
			transaction.setUser(user);
			transaction.setDocumentId(document.getId());
			transaction.setCategory(categorisationService.getCategoryForTransaction(nameMemo));
			transaction.setIsSubscription((byte) (categorisationService.isSubscription(nameMemo) ? 1 : 0));
			transactions.add(transaction);
		}

		public Set<TransactionBE> getTransactions() {
			return transactions;
		}
	}

	private class PeriodsSink implements FileParser.FileEntrySink {

		private Map<String, Long[]> periods = new HashMap<>();

		@Override
		public void onEntry(FileContent file, FileEntry entry) {
			Long[] period = periods.get(file.getFilename());

			if (period == null) {
				period = new Long[] { Long.MAX_VALUE, Long.MIN_VALUE };
				periods.put(file.getFilename(), period);
			}

			if (entry.datePosted.getTime() < period[0]) {
				period[0] = entry.datePosted.getTime();
			}
			if (entry.datePosted.getTime() > period[1]) {
				period[1] = entry.datePosted.getTime();
			}
		}

		public Map<String, Long[]> getPeriods() {
			return periods;
		}
	}
}
