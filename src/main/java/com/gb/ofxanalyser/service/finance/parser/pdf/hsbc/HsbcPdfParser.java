package com.gb.ofxanalyser.service.finance.parser.pdf.hsbc;

import java.io.FileOutputStream;
import java.util.Map;

import com.gb.ofxanalyser.service.finance.parser.FileParser;
import com.gb.ofxanalyser.service.finance.parser.ParseException;
import com.gb.ofxanalyser.service.finance.parser.TransactionAggregate;
import com.gb.ofxanalyser.service.finance.parser.TransactionItem;
import com.itextpdf.awt.geom.Rectangle2D;
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

			for (int i = 1; i <= reader.getNumberOfPages(); i++) {
				RowFinder finder1 = parser.processContent(i, new RowFinder("BALANCE BROUGHT FORWARD"));
				RowFinder finder2 = parser.processContent(i, new RowFinder("BALANCE CARRIED FORWARD"));

				if (finder1.getCount() == 1 && finder2.getCount() == 1 && finder1.getLly(0) > finder2.getLly(0)) {
					Rectangle2D.Float rect = new Rectangle2D.Float(0, finder2.getUry(0) + 1, Float.MAX_VALUE,
							finder1.getLly(0) - finder2.getUry(0) - 2);
					BoundedTextMarginFinder marginFinder = parser.processContent(i, new BoundedTextMarginFinder(rect));
					PdfContentByte cb = stamper.getOverContent(i);
					cb.setRGBColorStroke(255, 0, 0);
					cb.rectangle(marginFinder.getLlx(), marginFinder.getLly(), marginFinder.getWidth(),
							marginFinder.getHeight());
					cb.stroke();
				}

				// if (finder1.getCount() > 0) {
				// PdfContentByte cb = stamper.getOverContent(i);
				// cb.setRGBColorStroke(255, 0, 0);
				// cb.rectangle(finder1.getLlx(0), finder1.getLly(0),
				// finder1.getWidth(0), finder1.getHeight(0));
				// cb.stroke();
				// }

				// if (finder2.getCount() > 0) {
				// PdfContentByte cb = stamper.getOverContent(i);
				// cb.setRGBColorStroke(255, 0, 0);
				// cb.rectangle(finder2.getLlx(0), finder2.getLly(0),
				// finder2.getWidth(0), finder2.getHeight(0));
				// cb.stroke();
				// }
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
