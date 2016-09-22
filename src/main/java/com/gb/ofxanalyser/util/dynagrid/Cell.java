package com.gb.ofxanalyser.util.dynagrid;

public class Cell<T> {
	public T data;
	public Header<T> rowHead;
	public Header<T> colHead;

	@Override
	public String toString() {
		return "Cell [data=" + data + ", rowHead=" + rowHead.getPosition() + ", colHead=" + colHead.getPosition() + "]";
	}
}
