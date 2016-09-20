package com.gb.ofxanalyser.service.finance.parser.ofx;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import com.gb.ofxanalyser.service.finance.parser.FileParser;
import com.gb.ofxanalyser.service.finance.parser.ParseException;
import com.gb.ofxanalyser.service.finance.parser.TransactionAggregate;
import com.gb.ofxanalyser.service.finance.parser.TransactionItem;

import net.sf.ofx4j.domain.data.ResponseEnvelope;
import net.sf.ofx4j.domain.data.ResponseMessageSet;
import net.sf.ofx4j.domain.data.banking.BankStatementResponseTransaction;
import net.sf.ofx4j.domain.data.banking.BankingResponseMessageSet;
import net.sf.ofx4j.domain.data.common.Transaction;
import net.sf.ofx4j.domain.data.common.TransactionList;
import net.sf.ofx4j.io.AggregateUnmarshaller;
import net.sf.ofx4j.io.OFXParseException;

public class OfxParser implements FileParser {

	public void parse(byte[] file, Map<TransactionItem, TransactionAggregate> aggregate) throws ParseException {
		AggregateUnmarshaller<ResponseEnvelope> unmarshaller = new AggregateUnmarshaller<ResponseEnvelope>(
				ResponseEnvelope.class);
		try {
			ResponseEnvelope response = unmarshaller.unmarshal(new ByteArrayInputStream(file));

			for (ResponseMessageSet set : response.getMessageSets()) {
				if (set instanceof BankingResponseMessageSet) {
					BankingResponseMessageSet banking = (BankingResponseMessageSet) set;

					for (BankStatementResponseTransaction statementResponse : banking.getStatementResponses()) {
						TransactionList transactionList = statementResponse.getMessage().getTransactionList();

						for (Transaction transaction : transactionList.getTransactions()) {
							TransactionAggregate transactionInfo;
							TransactionItem bankTransaction = new TransactionItem(transaction);

							if (aggregate.containsKey(bankTransaction)) {
								transactionInfo = aggregate.get(bankTransaction);
							} else {
								transactionInfo = new TransactionAggregate();
								aggregate.put(bankTransaction, transactionInfo);
							}
							transactionInfo.transactions.add(bankTransaction);
							transactionInfo.total += bankTransaction.amount;
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new ParseException(e.getMessage());
		} catch (OFXParseException e) {
			e.printStackTrace();
			throw new ParseException(e.getMessage());
		}
	}
}
