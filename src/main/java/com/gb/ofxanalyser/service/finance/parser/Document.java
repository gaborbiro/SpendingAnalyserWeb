package com.gb.ofxanalyser.service.finance.parser;

public class Document {
	public final String title;
	private final byte[] content;

	public Document(String title, byte[] content) {
		this.title = title;
		this.content = content;
	}

	public String getTitle() {
		return title;
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
		return "Document [title=" + title + "]";
	}
}
