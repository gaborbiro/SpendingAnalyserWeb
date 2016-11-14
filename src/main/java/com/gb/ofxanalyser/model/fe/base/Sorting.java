package com.gb.ofxanalyser.model.fe.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gb.ofxanalyser.util.TextUtils;

public class Sorting {
	private int max;
	private List<Integer> sorting;

	@SuppressWarnings("unchecked")
	public Sorting(Sorting copyFrom) {
		this.max = copyFrom.max;
		this.sorting = (List<Integer>) ((ArrayList<Integer>) copyFrom.sorting).clone();
	}

	public Sorting(int max) {
		this.max = max;
		this.sorting = new ArrayList<>(max);
	}

	public void toggle(int criteria, boolean priority) {
		synchronized (sorting) {
			int index1;
			int index2;

			if ((index1 = sorting.indexOf(criteria)) >= 0) {
				// already sorting by it ASC
				sorting.remove(index1);
				if (priority) {
					sorting.add(0, -criteria);
				} else {
					sorting.add(-criteria);
				}
			} else if ((index2 = sorting.indexOf(-criteria)) >= 0) {
				// already sorting by it DESC
				sorting.remove(index2);
				// sorting.add(0, sort);
			} else {
				// not yet sorting by it
				if (sorting.size() == max) {
					// sorting queue full, remove something
					if (priority) {
						sorting.remove(max);
					} else {
						sorting.remove(0);
					}
				}
				// add new sorting criteria
				if (priority) {
					sorting.add(0, criteria);
				} else {
					sorting.add(criteria);
				}
			}
		}
	}

	public int isSet(int criteria) {
		synchronized (sorting) {
			int index;
			if ((index = sorting.indexOf(criteria)) >= 0) {
				return Integer.signum(criteria) * (index + 1);
			} else if ((index = sorting.indexOf(-criteria)) >= 0) {
				return Integer.signum(-criteria) * (index + 1);
			} else {
				return 0;
			}
		}
	}

	public int getCount() {
		return sorting.size();
	}

	public int getMax() {
		return max;
	}

	public int get(int index) {
		return sorting.get(index);
	}

	public void remove(int index) {
		sorting.remove(index);
	}
	
	@Override
	public String toString() {
		return "Sorting [sorting=" + sorting + "]";
	}

	public static int compare(String str1, String str2) {
		str1 = str1.trim();
		str2 = str2.trim();
		if (TextUtils.isEmpty(str1)) {
			return TextUtils.isEmpty(str2) ? 0 : -1;
		} else {
			if (TextUtils.isEmpty(str2)) {
				return 1;
			}
		}
		return str1.compareTo(str2);
	}

	public static int compare(boolean bool1, boolean bool2) {
		return (bool1 ? -1 : 1) - (bool2 ? -1 : 1);
	}

	public static int compare(Double dbl1, Double dbl2) {
		if (dbl1 == null) {
			return dbl2 == null ? 0 : -1;
		} else {
			if (dbl2 == null) {
				return 1;
			}
		}
		return dbl1.compareTo(dbl2);
	}

	public static int compare(Date date1, Date date2) {
		if (date1 == null) {
			return date2 == null ? 0 : -1;
		} else {
			if (date2 == null) {
				return 1;
			}
		}
		return date1.compareTo(date2);
	}

	public static int compare(long lng1, long lng2) {
		return (int) (lng1 - lng2);
	}
}
