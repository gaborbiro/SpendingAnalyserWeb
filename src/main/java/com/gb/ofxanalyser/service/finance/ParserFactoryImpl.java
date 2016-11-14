package com.gb.ofxanalyser.service.finance;

import java.util.ArrayList;
import java.util.List;

import com.gb.ofxanalyser.service.file.ParserFactory;
import com.gb.ofxanalyser.service.file.parser.FileContent;
import com.gb.ofxanalyser.service.file.parser.FileEntry;
import com.gb.ofxanalyser.service.file.parser.FileParser;
import com.gb.ofxanalyser.service.file.parser.hsbc.HsbcPdfParser;
import com.gb.ofxanalyser.service.file.parser.ofx.OfxParser;
import com.gb.ofxanalyser.service.file.parser.qif.QIFParser;
import com.gb.ofxanalyser.service.file.parser.revolut.RevolutPdfParser;

public class ParserFactoryImpl implements ParserFactory<FileEntry> {

	public List<FileParser<FileEntry>> getParserForFile(FileContent file) {
		List<FileParser<FileEntry>> parsers = new ArrayList<>();

		if (file.getFilename().endsWith("pdf")) {
			parsers.add(new HsbcPdfParser());
			parsers.add(new RevolutPdfParser());
		} else if (file.getFilename().endsWith("qif")) {
			parsers.add(new QIFParser());
		} else if (file.getFilename().endsWith("ofx")) {
			parsers.add(new OfxParser());
		}
		return parsers;
	}
}
