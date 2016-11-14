package com.gb.ofxanalyser.service.file;

import java.util.List;

import com.gb.ofxanalyser.service.file.parser.FileContent;
import com.gb.ofxanalyser.service.file.parser.FileParser;

public interface ParserFactory<E> {

	public List<FileParser<E>> getParserForFile(FileContent file);
}
