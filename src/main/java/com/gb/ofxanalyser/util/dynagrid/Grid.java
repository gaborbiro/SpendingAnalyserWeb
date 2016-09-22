package com.gb.ofxanalyser.util.dynagrid;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class Grid<I extends Comparable<I>, T> {
	TreeSet<Header<I, T>> colHeaders; // x
	TreeSet<Header<I, T>> rowHeaders; // y

	public Grid() {
		colHeaders = new TreeSet<Header<I, T>>(new Comparator<Header<I, T>>() {

			public int compare(Header<I, T> o1, Header<I, T> o2) {
				return o1.getIndex().compareTo(o2.getIndex());
			}
		});
		rowHeaders = new TreeSet<Header<I, T>>(new Comparator<Header<I, T>>() {

			public int compare(Header<I, T> o1, Header<I, T> o2) {
				return o2.getIndex().compareTo(o1.getIndex());
			}
		});
	}

	public void add(I x, I y, T data) {
		Header<I, T> colIndexCell = getColIndexCell(x);
		Header<I, T> rowIndexCell = getRowIndexCell(y);
		Cell<I, T> cell = new Cell<I, T>();
		cell.data = data;
		cell.colHead = colIndexCell;
		cell.rowHead = rowIndexCell;
		colIndexCell.add(cell);
		rowIndexCell.add(cell);
	}

	public void collapse(float from, float to) {

	}

	public Iterator<Header<I, T>> getColHeaders() {
		return colHeaders.iterator();
	}

	public Iterator<Header<I, T>> getRowHeaders() {
		return rowHeaders.iterator();
	}

	public Iterator<Iterator<T>> iterator() {
		return new Iterator<Iterator<T>>() {

			private Iterator<Header<I, T>> rowHeadI = rowHeaders.iterator();

			public boolean hasNext() {
				return rowHeadI.hasNext();
			}

			public Iterator<T> next() {
				final Header<I, T> rowHeader = rowHeadI.next();

				return new Iterator<T>() {

					private Iterator<Cell<I, T>> rowCellI = rowHeader.cells.iterator();
					private Iterator<Header<I, T>> colHeadI = colHeaders.iterator();
					private Cell<I, T> currentCell;

					public boolean hasNext() {
						return colHeadI.hasNext();
					}

					public T next() {
						Header<I, T> colHead = colHeadI.next();

						if (currentCell == null) {
							if (!rowCellI.hasNext()) {
								return null;
							}
							currentCell = rowCellI.next();
						}
						if (currentCell.colHead == colHead) {
							T data = currentCell.data;
							currentCell = null;
							return data;
						} else {
							return null;
						}
					}
				};
			}
		};
	}

	private Header<I, T> getColIndexCell(I index) {
		Comparator<Cell<I, T>> c = new Comparator<Cell<I, T>>() {

			public int compare(Cell<I, T> o1, Cell<I, T> o2) {
				return o2.rowHead.getIndex().compareTo(o1.rowHead.getIndex());
			}
		};
		return getIndexCell(colHeaders, index, c);
	}

	private Header<I, T> getRowIndexCell(I index) {
		Comparator<Cell<I, T>> c = new Comparator<Cell<I, T>>() {

			public int compare(Cell<I, T> o1, Cell<I, T> o2) {
				return o1.colHead.getIndex().compareTo(o2.colHead.getIndex());
			}
		};
		return getIndexCell(rowHeaders, index, c);
	}

	private Header<I, T> getIndexCell(Set<Header<I, T>> indexSet, I index, Comparator<Cell<I, T>> c) {
		for (Iterator<Header<I, T>> i = indexSet.iterator(); i.hasNext();) {
			Header<I, T> indexCell = i.next();

			if (index.equals(indexCell.getIndex())) {
				return indexCell;
			}
		}
		Header<I, T> indexCell = new Header<I, T>(index, c);
		indexSet.add(indexCell);
		return indexCell;
	}

	public int size() {
		return rowHeaders.size() * colHeaders.size();
	}
}
