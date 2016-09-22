package com.gb.ofxanalyser.util.dynagrid;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class Header<T> {
	private final float position;
	public final Set<Cell<T>> cells;

	public Header(float position, Comparator<Cell<T>> c) {
		this.position = position;
		cells = new TreeSet<Cell<T>>(c);
	}

	public boolean add(Cell<T> cell) {
		return cells.add(cell);
	}

	public float getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return "Header [position=" + position + ", cells=" + cells + "]";
	}
}
