package com.gb.ofxanalyser.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class PeekIterator<T> implements Iterator<T> {

	private final Iterator<T> iterator;
	private T nextitem;

	public PeekIterator(Iterator<T> iterator) {
		this.iterator = iterator;
	}

	public boolean hasNext() {
		if (nextitem != null) {
			return true;
		}

		if (iterator.hasNext()) {
			nextitem = iterator.next();
		}

		return nextitem != null;
	}

	public T next() {
		if (!hasNext()) {
			throw (new NoSuchElementException("Iterator has no elements left."));
		}

		T toReturn = nextitem;
		nextitem = null;
		return toReturn;
	}

	public T peek() {
		if (!hasNext()) {
			throw (new NoSuchElementException("Iterator has no elements left."));
		}

		return nextitem;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}