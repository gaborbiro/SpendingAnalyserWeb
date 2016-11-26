package com.gb.ofxanalyser.service.file.pdf.dynagrid;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class Header<I, T> {
	private final I index;
	public final Set<Cell<I, T>> cells;

	public Header(I index, Comparator<Cell<I, T>> c) {
		this.index = index;
		cells = new TreeSet<Cell<I, T>>(c);
	}

	public boolean add(Cell<I, T> cell) {
		return cells.add(cell);
	}

	public I getIndex() {
		return index;
	}

	@Override
	public String toString() {
		return "Header [index=" + index + ", cells=" + cells + "]";
	}
}
