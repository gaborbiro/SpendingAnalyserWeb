package com.gb.ofxanalyser.service.finance;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gb.ofxanalyser.model.be.CategoryStats;
import com.gb.ofxanalyser.model.be.TransactionBE;
import com.gb.ofxanalyser.model.be.UserBE;
import com.gb.ofxanalyser.model.be.UserDocumentBE;
import com.gb.ofxanalyser.model.fe.FileBucket;
import com.gb.ofxanalyser.service.file.FilesParser;
import com.gb.ofxanalyser.service.file.parser.FileContent;
import com.gb.ofxanalyser.service.file.parser.FileEntry;
import com.gb.ofxanalyser.service.file.parser.FileParser;
import com.gb.ofxanalyser.service.user.TransactionService;
import com.gb.ofxanalyser.service.user.UserDocumentService;
import com.gb.ofxanalyser.util.TextUtils;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

@Service("transactionsService")
public class TransactionsService {

	@Autowired
	UserDocumentService userDocumentService;

	@Autowired
	TransactionService transactionService;

	public void processDocuments(UserBE user, FileBucket fileBucket) throws IOException {
		UserDocumentBE document = null;
		MultipartFile[] multipartFiles = fileBucket.getFiles();

		for (MultipartFile multipartFile : multipartFiles) {
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
				} catch (ConstraintViolationException e) {
					// ignoring duplicates
					if (e.getSQLException() instanceof MySQLIntegrityConstraintViolationException) {
						System.out.println("Duplicate: " + transaction.getDescription());
					}
				}
			}
		}
	}

	/**
	 * The field UserDocumentBE.startDate/endDate will be updated
	 * 
	 * @throws IOException
	 */
	private Set<TransactionBE> processDocument(UserBE user, UserDocumentBE document, MultipartFile file)
			throws IOException {
		FilesParser.Builder<FileEntry> builder = FilesParser.builder(new ParserFactoryImpl());
		builder.with(file);

		TransactionsSink transactionsSink = new TransactionsSink(user);
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

	private class TransactionsSink implements FileParser.FileEntrySink<FileEntry> {

		private Set<TransactionBE> transactions;
		private UserBE user;

		public TransactionsSink(UserBE user) {
			this.user = user;
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
			transaction.setDate(entry.datePosted.getTime());
			transaction.setAmount(entry.amount * 100);
			transaction.setUser(user);
			transactions.add(transaction);
		}

		public Set<TransactionBE> getTransactions() {
			return transactions;
		}
	}

	private class PeriodsSink implements FileParser.FileEntrySink<FileEntry> {

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

	private class StatsSink implements FileParser.FileEntrySink<FileEntry> {

		private Set<CategoryStats> stats;

		@Override
		public void onEntry(FileContent file, FileEntry entry) {
			// TODO Auto-generated method stub

		}
	}
}
