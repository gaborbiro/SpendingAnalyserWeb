package com.gb.ofxanalyser.service.finance.parser.ofx;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gb.ofxanalyser.service.finance.parser.Document;
import com.gb.ofxanalyser.service.finance.parser.ParseException;
import com.gb.ofxanalyser.service.finance.parser.TransactionExtractor;
import com.gb.ofxanalyser.service.finance.parser.TransactionItem;

import net.sf.ofx4j.domain.data.ResponseEnvelope;
import net.sf.ofx4j.domain.data.ResponseMessageSet;
import net.sf.ofx4j.domain.data.banking.BankStatementResponseTransaction;
import net.sf.ofx4j.domain.data.banking.BankingResponseMessageSet;
import net.sf.ofx4j.domain.data.common.Transaction;
import net.sf.ofx4j.domain.data.common.TransactionList;
import net.sf.ofx4j.io.AggregateUnmarshaller;
import net.sf.ofx4j.io.OFXParseException;

public class OfxParser implements TransactionExtractor {

	public List<TransactionItem> getTransactions(Document file) throws ParseException {
		List<TransactionItem> result = new ArrayList<TransactionItem>();
		AggregateUnmarshaller<ResponseEnvelope> unmarshaller = new AggregateUnmarshaller<ResponseEnvelope>(
				ResponseEnvelope.class);
		
		try {
			String ofx = new String(file.getContent()).replaceAll(
					"(</CODE>|</SEVERITY>|</DTSERVER>|</LANGUAGE>|</TRNUID>|</CURDEF>|</BANKID>|</ACCTID>|"
					+ "</ACCTTYPE>|</DTSTART>|</DTEND>|</TRNTYPE>|</DTPOSTED>|</TRNAMT>|</FITID>|</NAME>|</MEMO>|"
					+ "</BALAMT>|</DTASOF>)", "");
			ResponseEnvelope response = unmarshaller.unmarshal(new ByteArrayInputStream(ofx.getBytes()));

			for (ResponseMessageSet set : response.getMessageSets()) {
				if (set instanceof BankingResponseMessageSet) {
					BankingResponseMessageSet banking = (BankingResponseMessageSet) set;

					for (BankStatementResponseTransaction statementResponse : banking.getStatementResponses()) {
						TransactionList transactionList = statementResponse.getMessage().getTransactionList();

						for (Transaction transaction : transactionList.getTransactions()) {
							TransactionItem transactionItem = new TransactionItem(transaction);
							transactionItem.name = "O " + transactionItem.name;
							result.add(transactionItem);
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
		return result;
	}
	

	@Override
	public String getParserName() {
		return "OFX Parser";
	}
}
