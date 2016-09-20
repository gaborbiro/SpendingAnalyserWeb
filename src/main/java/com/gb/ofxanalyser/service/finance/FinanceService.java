package com.gb.ofxanalyser.service.finance;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gb.ofxanalyser.model.Spending;
import com.gb.ofxanalyser.service.finance.parser.Document;
import com.gb.ofxanalyser.service.finance.parser.FileParser;
import com.gb.ofxanalyser.service.finance.parser.ParseException;
import com.gb.ofxanalyser.service.finance.parser.TransactionAggregate;
import com.gb.ofxanalyser.service.finance.parser.TransactionItem;
import com.gb.ofxanalyser.service.finance.parser.ofx.OfxParser;
import com.gb.ofxanalyser.service.finance.parser.pdf.hsbc.HsbcPdfParser;
import com.gb.ofxanalyser.service.finance.parser.pdf.revolut.RevolutPdfParser;

public class FinanceService {

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

		public static class Builder {
			private AggregationPolicy aggregationPolicy;
			private List<Document> files = new ArrayList<Document>();

			public Builder(AggregationPolicy aggregationPolicy) {
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

		public List<Spending> doAggregate() {
			Map<TransactionItem, TransactionAggregate> aggregate = new HashMap<TransactionItem, TransactionAggregate>();

			for (int i = 0; i < files.length; i++) {
				for (FileParser parser : getParsers(files[i])) {
					try {
						parser.parse(files[i].getContent(), aggregate);
						break;
					} catch (ParseException e) {
						// nothing to do, just try the next parser
					}
				}
			}

			TransactionAggregate[] transactionInfos = aggregate.values()
					.toArray(new TransactionAggregate[aggregate.size()]);
			Arrays.sort(transactionInfos);

			spendings = new ArrayList<Spending>();
			DecimalFormat df = new DecimalFormat(".##");

			for (TransactionAggregate transactionInfo : transactionInfos) {
				if (transactionInfo.transactions != null && transactionInfo.transactions.size() > 0) {
					StringBuffer buffer = new StringBuffer();
					TransactionItem bankTransaction = transactionInfo.transactions.get(0);
					buffer.append(bankTransaction.memo);
					buffer.append(" | ");
					buffer.append(bankTransaction.name);
					spendings.add(new Spending(buffer.toString(), df.format(transactionInfo.total)));
				}
			}
			return spendings;
		}

		private static FileParser[] getParsers(Document file) {
			if (file.title.endsWith("pdf")) {
				return new FileParser[] { new HsbcPdfParser(), new RevolutPdfParser(), new OfxParser() };
			} else {
				return new FileParser[] { new OfxParser(), new HsbcPdfParser(), new RevolutPdfParser() };
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
