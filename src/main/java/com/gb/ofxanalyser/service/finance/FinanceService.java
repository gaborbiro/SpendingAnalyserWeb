package com.gb.ofxanalyser.service.finance;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.springframework.stereotype.Service;

import com.gb.ofxanalyser.model.Sorting;
import com.gb.ofxanalyser.model.Spending;
import com.gb.ofxanalyser.service.finance.parser.Document;
import com.gb.ofxanalyser.service.finance.parser.ParseException;
import com.gb.ofxanalyser.service.finance.parser.TransactionExtractor;
import com.gb.ofxanalyser.service.finance.parser.TransactionItem;
import com.gb.ofxanalyser.service.finance.parser.hsbc.HsbcPdfParser;
import com.gb.ofxanalyser.service.finance.parser.mapping.MappingService;
import com.gb.ofxanalyser.service.finance.parser.ofx.OfxParser;
import com.gb.ofxanalyser.service.finance.parser.qif.QIFParser;
import com.gb.ofxanalyser.service.finance.parser.revolut.RevolutPdfParser;
import com.gb.ofxanalyser.util.TextUtils;

@Service("financeService")
public class FinanceService {

	private static NumberFormat DECIMAL_FORMAT = DecimalFormat.getCurrencyInstance();
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MMM");

	public Builder builder(AggregationPolicy aggregationPolicy) {
		return new Builder(aggregationPolicy);
	}

	public static class Builder {
		private AggregationPolicy aggregationPolicy;
		private List<Document> files = new ArrayList<Document>();

		private Builder(AggregationPolicy aggregationPolicy) {
			this.aggregationPolicy = aggregationPolicy;
		}

		public Builder with(String title, byte[] content) {
			files.add(new Document(title, content));
			return this;
		}

		public SpendingAggregator build() {
			return new SpendingAggregator(aggregationPolicy, files.toArray(new Document[files.size()]));
		}
	}

	/**
	 * Holds a spending history aggregated by some {@link AggregationPolicy}.
	 */
	public static class SpendingAggregator {

		private AggregationPolicy aggregationPolicy;
		private Document[] files;

		private Spending[] spendings;

		private SpendingAggregator(AggregationPolicy aggregationPolicy, Document... files) {
			this.aggregationPolicy = aggregationPolicy;
			this.files = files;
		}

		/**
		 * Heavy lifting happens here
		 */
		public Spending[] doAggregate(Sorting sorting) {
			final Sorting finalSorting = sorting.clone();
			// Adding attributes that are part of identity, while respecting the
			// users sorting order. We don't want Spendings with same name to
			// override each other just because the user is sorting only by name
			if (finalSorting.isSet(Sorting.SORT_VAL_ASC) == 0) {
				finalSorting.toggleSortByAmount(false);
			}

			if (finalSorting.isSet(Sorting.SORT_DAT_ASC) == 0) {
				finalSorting.toggleSortByDate(false);
			}

			if (finalSorting.isSet(Sorting.SORT_MEM_ASC) == 0) {
				finalSorting.toggleSortByNameMemo(false);
			}

			TreeSet<Spending> spendings = new TreeSet<Spending>(new Comparator<Spending>() {

				@Override
				public int compare(Spending o1, Spending o2) {
					int r = 0;

					for (int i = 0; i < finalSorting.getCount() && r == 0; i++) {
						r = Sorting.compare(finalSorting.get(i), o1, o2);
					}
					return r;
				}
			});
			int index = 0;

			MappingService mappingDB = new MappingService();

			for (int i = 0; i < files.length; i++) {
				System.out.print("Parsing '" + files[i].title + "'");
				TransactionExtractor[] parsers = getParsers(files[i]);
				TransactionExtractor success = null;

				for (int j = 0; j < parsers.length && success == null; j++) {
					System.out.print(".");
					try {
						List<TransactionItem> transactionItems = parsers[j].getTransactions(files[i]);

						for (TransactionItem transactionInfo : transactionItems) {
							StringBuffer buffer = new StringBuffer();
							buffer.append(transactionInfo.payee);
							if (!TextUtils.isEmpty(transactionInfo.memo)) {
								buffer.append("<br>");
								buffer.append(transactionInfo.memo);
							}

							String nameMemo = buffer.toString();
							String category = mappingDB.getCategoryForSpending(nameMemo);

							spendings.add(new Spending(index++, nameMemo, transactionInfo.datePosted,
									DATE_FORMAT.format(transactionInfo.datePosted), transactionInfo.amount,
									DECIMAL_FORMAT.format(transactionInfo.amount), category,
									mappingDB.isSubscription(nameMemo)));
						}
						if (!transactionItems.isEmpty()) {
							success = parsers[j];
							System.out.println(" success: " + success.getParserName() + " - " + transactionItems.size()
									+ " transactions");
						}
					} catch (ParseException e) {
						// nothing to do, just try the next parser
					}
				}

				if (success == null) {
					System.out.println(" fail");
				}
			}
			this.spendings = (Spending[]) spendings.toArray(new Spending[spendings.size()]);
			return this.spendings;
		}

		private static TransactionExtractor[] getParsers(Document file) {
			if (file.title.endsWith("pdf")) {
				return new TransactionExtractor[] { new HsbcPdfParser(), new RevolutPdfParser() };
			} else if (file.title.endsWith("qif")) {
				return new TransactionExtractor[] { new QIFParser() };
			} else if (file.title.endsWith("ofx")) {
				return new TransactionExtractor[] { new OfxParser() };
			} else {
				return new TransactionExtractor[0];
			}
		}

		public Spending[] getSpendings() {
			return spendings;
		}
	}

	/**
	 * Defines a policy by which items in a spending history are aggregated.<br>
	 * For eg: items that are instances of shopping in the same grocery store or
	 * items that are the same type of phone bills
	 */
	public interface AggregationPolicy {
		public boolean equals(TransactionItem a, TransactionItem b);
	}
}
