package com.gb.ofxanalyser.service.file.parser.ofx;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.gb.ofxanalyser.service.file.parser.FileContent;
import com.gb.ofxanalyser.service.file.parser.FileEntry;
import com.gb.ofxanalyser.service.file.parser.FileParser;
import com.gb.ofxanalyser.service.file.parser.ParseException;

import net.sf.ofx4j.domain.data.ResponseEnvelope;
import net.sf.ofx4j.domain.data.ResponseMessageSet;
import net.sf.ofx4j.domain.data.banking.BankStatementResponseTransaction;
import net.sf.ofx4j.domain.data.banking.BankingResponseMessageSet;
import net.sf.ofx4j.domain.data.common.Transaction;
import net.sf.ofx4j.domain.data.common.TransactionList;
import net.sf.ofx4j.io.AggregateUnmarshaller;
import net.sf.ofx4j.io.OFXParseException;

public class OfxParser implements FileParser {

	public int parse(FileContent file, FileEntrySink listener) throws ParseException {
		int entryCount = 0;
		AggregateUnmarshaller<ResponseEnvelope> unmarshaller = new AggregateUnmarshaller<ResponseEnvelope>(
				ResponseEnvelope.class);

		try {
			String ofx = new String(file.getContent())
					.replaceAll("(</CODE>|</SEVERITY>|</DTSERVER>|</LANGUAGE>|</TRNUID>|</CURDEF>|</BANKID>|</ACCTID>|"
							+ "</ACCTTYPE>|</DTSTART>|</DTEND>|</TRNTYPE>|</DTPOSTED>|</TRNAMT>|</FITID>|</NAME>|</MEMO>|"
							+ "</BALAMT>|</DTASOF>)", "");
			ResponseEnvelope response = unmarshaller.unmarshal(new ByteArrayInputStream(ofx.getBytes()));

			for (ResponseMessageSet set : response.getMessageSets()) {
				if (set instanceof BankingResponseMessageSet) {
					BankingResponseMessageSet banking = (BankingResponseMessageSet) set;

					for (BankStatementResponseTransaction statementResponse : banking.getStatementResponses()) {
						TransactionList transactionList = statementResponse.getMessage().getTransactionList();

						for (Transaction transaction : transactionList.getTransactions()) {
							listener.onEntry(file, new FileEntry(transaction));
							entryCount++;
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
		return entryCount;
	}

	@Override
	public String getConverterName() {
		return "OFX Parser";
	}
}
