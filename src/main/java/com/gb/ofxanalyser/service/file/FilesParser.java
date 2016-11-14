package com.gb.ofxanalyser.service.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.gb.ofxanalyser.service.file.parser.FileContent;
import com.gb.ofxanalyser.service.file.parser.FileParser;
import com.gb.ofxanalyser.service.file.parser.FileParser.FileEntrySink;
import com.gb.ofxanalyser.service.file.parser.ParseException;

/**
 * Extracts the complete list of entries from the uploaded files
 */
public class FilesParser<E> implements FileEntrySink<E> {

	public static <E> Builder<E> builder(ParserFactory<E> parserFactory) {
		return new Builder<E>(parserFactory);
	}

	public static class Builder<E> {
		private List<FileParser.FileEntrySink<E>> listeners = new ArrayList<>();
		private ParserFactory<E> parserFactory;
		private List<FileContent> files = new ArrayList<>();

		public Builder(ParserFactory<E> parserFactory) {
			this.parserFactory = parserFactory;
		}

		public Builder<E> with(MultipartFile multipartFile) throws IOException {
			files.add(new FileContent(multipartFile.getOriginalFilename(), multipartFile.getBytes()));
			return this;
		}

		public Builder<E> sink(FileParser.FileEntrySink<E> listener) {
			listeners.add(listener);
			return this;
		}

		public FilesParser<E> build() {
			return new FilesParser<E>(listeners, parserFactory, files.toArray(new FileContent[files.size()]));
		}
	}

	private FileContent[] files;
	private ParserFactory<E> parserFactory;

	private List<FileParser.FileEntrySink<E>> listeners;

	private FilesParser(List<FileParser.FileEntrySink<E>> listeners, ParserFactory<E> parserFactory,
			FileContent... files) {
		this.listeners = listeners;
		this.parserFactory = parserFactory;
		this.files = files;
	}

	/**
	 * Heavy lifting happens here. Synchronous. See {@link FileEntrySink} for
	 * results.
	 */
	public void process() {
		for (int i = 0; i < files.length; i++) {
			handleFile(files[i]);
		}
	}

	private void handleFile(FileContent file) {
		System.out.println("Parsing '" + file.getFilename() + "'");
		boolean success = false;

		for (FileParser<E> parser : parserFactory.getParserForFile(file)) {
			try {
				int transactionCount = parser.parse(file, this);

				if (transactionCount > 0) {
					System.out.println(
							"   success: " + parser.getConverterName() + " - " + transactionCount + " transactions");
					success = true;
				} else {
					System.out.println("   failed: " + parser.getConverterName());
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
	public void onEntry(FileContent file, E entry) {
		for (FileEntrySink<E> listener : listeners) {
			listener.onEntry(file, entry);
		}
	}
}