package com.gb.ofxanalyser.service.finance;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gb.ofxanalyser.model.Spending;
import com.gb.ofxanalyser.service.finance.parser.FileParser;
import com.gb.ofxanalyser.service.finance.parser.TransactionAggregate;
import com.gb.ofxanalyser.service.finance.parser.TransactionItem;
import com.gb.ofxanalyser.service.finance.parser.ofx.OfxParser;
import com.gb.ofxanalyser.service.finance.parser.pdf.hsbc.HsbcPdfParser;

public class FinanceService {

	/**
	 * Holds a spending history aggregated by some {@link AggregationPolicy}.
	 */
	public static class SpendingAggregator {

		private AggregationPolicy aggregationPolicy;
		private byte[][] files;
		private Type[] types;

		private List<Spending> spendings;

		private SpendingAggregator(AggregationPolicy aggregationPolicy, byte[][] files, Type[] types) {
			this.aggregationPolicy = aggregationPolicy;
			this.files = files;
			this.types = types;
		}

		public static class Builder {

			private AggregationPolicy aggregationPolicy;
			private List<byte[]> files = new ArrayList<byte[]>();
			private List<Type> types = new ArrayList<FinanceService.Type>();

			public Builder(AggregationPolicy aggregationPolicy) {
				this.aggregationPolicy = aggregationPolicy;
			}

			public Builder file(byte[] file, Type type) {
				files.add(file);
				types.add(type);
				return this;
			}

			public SpendingAggregator build() {
				SpendingAggregator aggregator = new SpendingAggregator(aggregationPolicy,
						files.toArray(new byte[files.size()][]), types.toArray(new Type[types.size()]));
				aggregator.doAggregate();
				return aggregator;
			}
		}

		private void doAggregate() {
			Map<TransactionItem, TransactionAggregate> aggregate = new HashMap<TransactionItem, TransactionAggregate>();

			for (int i = 0; i < files.length; i++) {
				FileParser parser = getParser(types[i]);

				if (parser != null) {
					parser.parse(files[i], aggregate);
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

	public static FileParser getParser(Type type) {
		switch (type) {
		case OFX:
			return new OfxParser();
		case HSBC_PDF:
			return new HsbcPdfParser();
		case REVOLUT_PDF:
			return new FileParser() {

				public void parse(byte[] file, Map<TransactionItem, TransactionAggregate> transactionAggregate) {
				}
			};
		}
		return null;
	}

	public enum Type {
		OFX, HSBC_PDF, REVOLUT_PDF
	}
}
