package com.gb.ofxanalyser.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gb.ofxanalyser.util.TextUtils;

public class Sorting {

	public static final int SORT_MEM_ASC = 1;
	public static final int SORT_CAT_ASC = 2;
	public static final int SORT_SUB_ASC = 3;
	public static final int SORT_DAT_ASC = 4;
	public static final int SORT_VAL_ASC = 5;

	public static final int SORT_MEM_DSC = -1;
	public static final int SORT_CAT_DSC = -2;
	public static final int SORT_SUB_DSC = -3;
	public static final int SORT_DAT_DSC = -4;
	public static final int SORT_VAL_DSC = -5;

	// increase the capacity if you add more sorting options
	private List<Integer> sorting = new ArrayList<>(5);

	private void toggle(int sort, boolean priority) {
		synchronized (sorting) {
			int index1;
			int index2;

			if ((index1 = sorting.indexOf(sort)) >= 0) {
				sorting.remove(index1);
				if (priority) {
					sorting.add(0, -sort);
				} else {
					sorting.add(-sort);
				}
			} else if ((index2 = sorting.indexOf(-sort)) >= 0) {
				sorting.remove(index2);
				sorting.add(0, sort);
			} else {
				if (sorting.size() == 5) {
					if (priority) {
						sorting.remove(4);
					} else {
						sorting.remove(0);
					}
				}
				if (priority) {
					sorting.add(0, sort);
				} else {
					sorting.add(sort);
				}
			}
		}
	}

	public int isSet(int sort) {
		synchronized (sorting) {
			if (sorting.indexOf(sort) >= 0) {
				return Integer.signum(sort);
			} else if (sorting.indexOf(-sort) >= 0) {
				return Integer.signum(-sort);
			} else {
				return 0;
			}
		}
	}

	public int getCount() {
		return sorting.size();
	}

	public int get(int index) {
		return sorting.get(index);
	}

	public void toggleSortByNameMemo(boolean priority) {
		toggle(SORT_MEM_ASC, priority);
	}

	public int getSortByNameMemo() {
		return isSet(SORT_MEM_ASC);
	}

	public void toggleSortByCategory(boolean priority) {
		toggle(SORT_CAT_ASC, priority);
	}

	public int getSortByCategory() {
		return isSet(SORT_CAT_ASC);
	}

	public void toggleSortByIsSubscription(boolean priority) {
		toggle(SORT_SUB_ASC, priority);
	}

	public int getSortByIsSubscription() {
		return isSet(SORT_SUB_ASC);
	}

	public void toggleSortByDate(boolean priority) {
		toggle(SORT_DAT_ASC, priority);
	}

	public int getSortByDate() {
		return isSet(SORT_DAT_ASC);
	}

	public void toggleSortByAmount(boolean priority) {
		toggle(SORT_VAL_ASC, priority);
	}

	public int getSortByAmount() {
		return isSet(SORT_VAL_ASC);
	}

	@Override
	public String toString() {
		return "Sorting [sorting=" + sorting + "]";
	}

	public static int compare(int sort, Spending spending1, Spending spending2) {
		switch (sort) {
		case SORT_MEM_ASC:
			return compareStr(spending1.getDescription(), spending2.getDescription());
		case SORT_MEM_DSC:
			return compareStr(spending2.getDescription(), spending1.getDescription());
		case SORT_CAT_ASC:
			return compareStr(spending1.getCategory(), spending2.getCategory());
		case SORT_CAT_DSC:
			return compareStr(spending2.getCategory(), spending1.getCategory());
		case SORT_SUB_ASC:
			return compareBool(spending1.isSubscription(), spending2.isSubscription());
		case SORT_SUB_DSC:
			return compareBool(spending2.isSubscription(), spending1.isSubscription());
		case SORT_VAL_ASC:
			return compareDbl(spending1.getActualAmount(), spending2.getActualAmount());
		case SORT_VAL_DSC:
			return compareDbl(spending2.getActualAmount(), spending1.getActualAmount());
		case SORT_DAT_ASC:
			return compareDate(spending1.getActualDate(), spending2.getActualDate());
		case SORT_DAT_DSC:
			return compareDate(spending2.getActualDate(), spending1.getActualDate());
		default:
			return 0;
		}
	}

	private static int compareStr(String str1, String str2) {
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

	private static int compareBool(boolean bool1, boolean bool2) {
		return (bool1 ? 1 : -1) - (bool2 ? 1 : -1);
	}

	private static int compareDbl(Double dbl1, Double dbl2) {
		if (dbl1 == null) {
			return dbl2 == null ? 0 : -1;
		} else {
			if (dbl2 == null) {
				return 1;
			}
		}
		return dbl1.compareTo(dbl2);
	}

	private static int compareDate(Date date1, Date date2) {
		if (date1 == null) {
			return date2 == null ? 0 : -1;
		} else {
			if (date2 == null) {
				return 1;
			}
		}
//		date1 = (Date) date1.clone();
//		date1.setHours(0);
//		date1.setMinutes(0);
//		date1.setSeconds(0);
//		date2 = (Date) date2.clone();
//		date2.setHours(0);
//		date2.setMinutes(0);
//		date2.setSeconds(0);
		return date1.compareTo(date2);
	}

	public Sorting clone() {
		Sorting clone = new Sorting();
		clone.sorting = (List<Integer>) ((ArrayList) sorting).clone();
		return clone;
	}
}
