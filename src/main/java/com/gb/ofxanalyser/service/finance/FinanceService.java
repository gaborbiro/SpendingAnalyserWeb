package com.gb.ofxanalyser.service.finance;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gb.ofxanalyser.model.Spending;
import com.gb.ofxanalyser.service.finance.parser.Document;
import com.gb.ofxanalyser.service.finance.parser.TransactionExtractor;
import com.gb.ofxanalyser.service.finance.parser.ParseException;
import com.gb.ofxanalyser.service.finance.parser.TransactionItem;
import com.gb.ofxanalyser.service.finance.parser.hsbc.HsbcPdfParser;
import com.gb.ofxanalyser.service.finance.parser.ofx.OfxParser;
import com.gb.ofxanalyser.service.finance.parser.pdf.itext.PdfParserImpl;
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

		private List<Spending> spendings;

		private SpendingAggregator(AggregationPolicy aggregationPolicy, Document... files) {
			this.aggregationPolicy = aggregationPolicy;
			this.files = files;
		}

		/**
		 * Heavy lifting happens here
		 */
		public List<Spending> doAggregate() {
			spendings = new ArrayList<Spending>();

			for (int i = 0; i < files.length; i++) {
				for (TransactionExtractor parser : getParsers(files[i])) {
					try {
						List<TransactionItem> transactionItems = parser.getTransactions();

						for (TransactionItem transactionInfo : transactionItems) {
							StringBuffer buffer = new StringBuffer();
							buffer.append(transactionInfo.name);
							if (!TextUtils.isEmpty(transactionInfo.memo)) {
								buffer.append("\n");
								buffer.append(transactionInfo.memo);
							}
							spendings
									.add(new Spending(buffer.toString(), DATE_FORMAT.format(transactionInfo.datePosted),
											DECIMAL_FORMAT.format(transactionInfo.amount)));
						}
						// successfully parsed the file, moving on to the next file 
						break;
					} catch (ParseException e) {
						// nothing to do, just try the next parser
					}
				}
			}
			return spendings;
		}

		private static TransactionExtractor[] getParsers(Document file) {
			if (file.title.endsWith("pdf")) {
				return new TransactionExtractor[] { new HsbcPdfParser(new PdfParserImpl(file)), new RevolutPdfParser() };
			} else {
				return new TransactionExtractor[] { new OfxParser(file) };
			}
		}

		public List<Spending> getSpendings() {
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
