package com.gb.ofxanalyser.service.file.pdf.dynagrid;

public class Cell<I, T> {
	public T data;
	public Header<I, T> rowHead;
	public Header<I, T> colHead;

	@Override
	public String toString() {
		return "Cell [data=" + data + ", rowHead=" + rowHead.getIndex() + ", colHead=" + colHead.getIndex() + "]";
	}
}
