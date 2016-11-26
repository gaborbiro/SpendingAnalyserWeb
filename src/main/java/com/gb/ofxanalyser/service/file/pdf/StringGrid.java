package com.gb.ofxanalyser.service.file.pdf;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.gb.ofxanalyser.service.file.pdf.dynagrid.Cell;
import com.gb.ofxanalyser.service.file.pdf.dynagrid.Grid;
import com.gb.ofxanalyser.service.file.pdf.dynagrid.Header;

public class StringGrid extends Grid<Float, String> {

	/**
	 * Collapse all columns (must specify a concatenation strategy) the index of
	 * which satisfies: from <= index <= to
	 */
	public void collapse(Float from, Float to, String separator) {
		Comparator<Cell<Float, String>> c = new Comparator<Cell<Float, String>>() {

			public int compare(Cell<Float, String> o1, Cell<Float, String> o2) {
				return o2.rowHead.getIndex().compareTo(o1.rowHead.getIndex());
			}
		};
		Header<Float, String> resultColHeader = new Header<Float, String>(from, c);

		Set<Header<Float, String>> colsToRemove = new TreeSet<Header<Float, String>>(
				new Comparator<Header<Float, String>>() {

					public int compare(Header<Float, String> o1, Header<Float, String> o2) {
						return o1.getIndex().compareTo(o2.getIndex());
					}
				});

		for (Iterator<Iterator<Cell<Float, String>>> rowI = iterator(from, to); rowI.hasNext();) {
			List<String> data = new ArrayList<String>();
			List<Cell<Float, String>> toRemove = new ArrayList<Cell<Float, String>>();
			Header<Float, String> rowHead = null;

			for (Iterator<Cell<Float, String>> cellI = rowI.next(); cellI.hasNext();) {
				Cell<Float, String> cell = cellI.next();
				if (cell != null) {
					data.add(cell.data);
					// cell.rowHead.cells.remove(cell);
					toRemove.add(cell);

					if (rowHead == null) {
						rowHead = cell.rowHead;
					}
					colsToRemove.add(cell.colHead);
				}
			}
			if (rowHead != null) {
				rowHead.cells.removeAll(toRemove);
				String newData = data.stream().collect(Collectors.joining(separator));
				Cell<Float, String> newCell = new Cell<Float, String>();
				newCell.colHead = resultColHeader;
				newCell.rowHead = rowHead;
				newCell.data = newData;
				resultColHeader.cells.add(newCell);
				rowHead.cells.add(newCell);
			}
		}
		colHeaders.removeAll(colsToRemove);
		colHeaders.add(resultColHeader);
	}
}
