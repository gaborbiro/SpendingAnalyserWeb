package com.gb.ofxanalyser.util.dynagrid;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class Grid<T> {
	TreeSet<Header<T>> colHeaders; // x
	TreeSet<Header<T>> rowHeaders; // y

	public Grid() {
		colHeaders = new TreeSet<Header<T>>(new Comparator<Header<T>>() {

			public int compare(Header<T> o1, Header<T> o2) {
				float f = o1.getPosition() - o2.getPosition();
				return f < 0 ? -1 : (f > 0 ? 1 : 0);
			}
		});
		rowHeaders = new TreeSet<Header<T>>(new Comparator<Header<T>>() {

			public int compare(Header<T> o1, Header<T> o2) {
				float f = o2.getPosition() - o1.getPosition();
				return f < 0 ? -1 : (f > 0 ? 1 : 0);
			}
		});
	}

	public void add(float x, float y, T data) {
		Header<T> colIndexCell = getColIndexCell(x);
		Header<T> rowIndexCell = getRowIndexCell(y);
		Cell<T> cell = new Cell<T>();
		cell.data = data;
		cell.colHead = colIndexCell;
		cell.rowHead = rowIndexCell;
		colIndexCell.add(cell);
		rowIndexCell.add(cell);
	}

	public Iterator<Header<T>> getColHeaders() {
		return colHeaders.iterator();
	}

	public Iterator<Header<T>> getRowHeaders() {
		return rowHeaders.iterator();
	}

	public Iterator<Iterator<T>> iterator() {
		return new Iterator<Iterator<T>>() {

			private Iterator<Header<T>> rowHeadI = rowHeaders.iterator();

			public boolean hasNext() {
				return rowHeadI.hasNext();
			}

			public Iterator<T> next() {
				final Header<T> rowHeader = rowHeadI.next();

				return new Iterator<T>() {

					private Iterator<Cell<T>> rowCellI = rowHeader.cells.iterator();
					private Iterator<Header<T>> colHeadI = colHeaders.iterator();
					private Cell<T> currentCell;

					public boolean hasNext() {
						return colHeadI.hasNext();
					}

					public T next() {
						Header<T> colHead = colHeadI.next();

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
//							return (T) String.format("%.2f", currentCell.colHead.getPosition());
							return null;
						}
					}
				};
			}
		};
	}

	private Header<T> getColIndexCell(float position) {
		Comparator<Cell<T>> c = new Comparator<Cell<T>>() {

			public int compare(Cell<T> o1, Cell<T> o2) {
				return (int) (o2.rowHead.getPosition() - o1.rowHead.getPosition());
			}
		};
		return getIndexCell(colHeaders, position, c);
	}

	private Header<T> getRowIndexCell(float position) {
		Comparator<Cell<T>> c = new Comparator<Cell<T>>() {

			public int compare(Cell<T> o1, Cell<T> o2) {
				return (int) (o1.colHead.getPosition() - o2.colHead.getPosition());
			}
		};
		return getIndexCell(rowHeaders, position, c);
	}

	private Header<T> getIndexCell(Set<Header<T>> indexSet, float position, Comparator<Cell<T>> c) {
		for (Iterator<Header<T>> i = indexSet.iterator(); i.hasNext();) {
			Header<T> indexCell = i.next();

			if (position == indexCell.getPosition()) {
				return indexCell;
			}
		}
		Header<T> indexCell = new Header<T>(position, c);
		indexSet.add(indexCell);
		return indexCell;
	}

	public int size() {
		return rowHeaders.size() * colHeaders.size();
	}
}
