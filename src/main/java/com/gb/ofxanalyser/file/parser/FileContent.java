package com.gb.ofxanalyser.file.parser;

public class FileContent {
	private final String filename;
	private final byte[] content;

	public FileContent(String filename, byte[] content) {
		this.filename = filename;
		this.content = content;
	}

	public String getFilename() {
		return filename;
	}

	public byte[] getContent() {
		if (content == null) {
			return null;
		}
		byte[] copy = new byte[content.length];
		System.arraycopy(content, 0, copy, 0, content.length);
		return copy;
	}

	@Override
	public String toString() {
		return "FileContent [filename=" + filename + "]";
	}
}
