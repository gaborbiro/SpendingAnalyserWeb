package com.gb.ofxanalyser.service.finance.parser.pdf.hsbc;

import java.io.FileOutputStream;
import java.util.Map;

import com.gb.ofxanalyser.service.finance.parser.FileParser;
import com.gb.ofxanalyser.service.finance.parser.ParseException;
import com.gb.ofxanalyser.service.finance.parser.TransactionAggregate;
import com.gb.ofxanalyser.service.finance.parser.TransactionItem;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;

public class HsbcPdfParser implements FileParser {

	public void parse(byte[] file, Map<TransactionItem, TransactionAggregate> transactionAggregate)
			throws ParseException {
		PdfReader reader = null;
		PdfStamper stamper = null;
		try {
			reader = new PdfReader(file);
			PdfReaderContentParser parser = new PdfReaderContentParser(reader);
			stamper = new PdfStamper(reader, new FileOutputStream("c:\\Downloads\\statement_out.pdf"));
			RowFinder finder;

			for (int i = 1; i <= reader.getNumberOfPages(); i++) {
				finder = parser.processContent(i, new RowFinder("BALANCE BROUGHT FORWARD"));

				if (finder.getCount() > 0) {
					PdfContentByte cb = stamper.getOverContent(i);
					cb.setRGBColorStroke(255, 0, 0);
					cb.rectangle(finder.getLlx(0), finder.getLly(0), finder.getWidth(0), finder.getHeight(0));
					cb.stroke();
				}

				finder = parser.processContent(i, new RowFinder("BALANCE CARRIED FORWARD"));

				if (finder.getCount() > 0) {
					PdfContentByte cb = stamper.getOverContent(i);
					cb.setRGBColorStroke(255, 0, 0);
					cb.rectangle(finder.getLlx(0), finder.getLly(0), finder.getWidth(0), finder.getHeight(0));
					cb.stroke();
				}
			}
			stamper.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
				stamper.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
