package com.gb.ofxanalyser.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.gb.ofxanalyser.file.parser.FileContent;
import com.gb.ofxanalyser.file.parser.FileEntry;
import com.gb.ofxanalyser.file.parser.FileParser;
import com.gb.ofxanalyser.file.parser.ParseException;
import com.gb.ofxanalyser.file.parser.FileParser.FileEntrySink;
import com.gb.ofxanalyser.file.parser.hsbc.HsbcPdfParser;
import com.gb.ofxanalyser.file.parser.ofx.OfxParser;
import com.gb.ofxanalyser.file.parser.qif.QIFParser;
import com.gb.ofxanalyser.file.parser.revolut.RevolutPdfParser;

/**
 * Extracts the complete list of entries from the uploaded files
 */
public class FileParserService implements FileEntrySink {

	public static class Builder {
		private List<FileParser.FileEntrySink> listeners = new ArrayList<>();
		private List<FileContent> files = new ArrayList<>();

		public Builder() {
		}

		public Builder with(MultipartFile multipartFile) throws IOException {
			files.add(new FileContent(multipartFile.getOriginalFilename(), multipartFile.getBytes()));
			return this;
		}

		public Builder sink(FileParser.FileEntrySink listener) {
			listeners.add(listener);
			return this;
		}

		public FileParserService build() {
			return new FileParserService(listeners, files.toArray(new FileContent[files.size()]));
		}
	}

	private FileContent[] files;

	private List<FileParser.FileEntrySink> listeners;

	private FileParserService(List<FileParser.FileEntrySink> listeners, FileContent... files) {
		this.listeners = listeners;
		this.files = files;
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Heavy lifting happens here. Synchronous. See {@link FileEntrySink} for
	 * results.
	 */
	public void process() {
		for (int i = 0; i < files.length; i++) {
			processFile(files[i]);
		}
	}

	private void processFile(FileContent file) {
		System.out.println("Parsing '" + file.getFilename() + "'");
		boolean success = false;

		for (FileParser parser : getParserForFile(file)) {
			System.out.print(parser.getConverterName() + "...");
			try {
				int transactionCount = parser.parse(file, this);
				if (transactionCount > 0) {
					System.out.println("   success: " + transactionCount + " transactions");
					success = true;
				} else {
					System.out.println("failed");
				}
			} catch (ParseException e) {
				// nothing to do, just try the next parser
			}
		}

		if (!success) {
			System.out.println(" fail");
		}
	}

	@Override
	public void onEntry(FileContent file, FileEntry entry) {
		for (FileEntrySink listener : listeners) {
			listener.onEntry(file, entry);
		}
	}

	private static List<FileParser> getParserForFile(FileContent file) {
		List<FileParser> parsers = new ArrayList<>();

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