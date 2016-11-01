package com.gb.ofxanalyser.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gb.ofxanalyser.util.TextUtils;

public class Sorting {

	private static final int SORT_MEM_ASC = 1;
	private static final int SORT_CAT_ASC = 2;
	private static final int SORT_SUB_ASC = 3;
	private static final int SORT_DAT_ASC = 4;
	private static final int SORT_VAL_ASC = 5;

	private static final int SORT_MEM_DSC = -1;
	private static final int SORT_CAT_DSC = -2;
	private static final int SORT_SUB_DSC = -3;
	private static final int SORT_DAT_DSC = -4;
	private static final int SORT_VAL_DSC = -5;

	// increase the capacity if you add more sorting options
	private List<Integer> sorting = new ArrayList<>(5);

	private void toggle(int sort) {
		int index1;
		int index2;

		if ((index1 = sorting.indexOf(sort)) >= 0) {
			sorting.set(index1, -sort);
		} else if ((index2 = sorting.indexOf(-sort)) >= 0) {
			sorting.set(index2, sort);
		} else {
			if (sorting.size() == 5) {
				sorting.remove(0);
			}
			sorting.add(sort);
		}
	}

	private int isSet(int sort) {
		if (sorting.indexOf(sort) >= 0) {
			return Integer.signum(sort);
		} else if (sorting.indexOf(-sort) >= 0) {
			return Integer.signum(-sort);
		} else {
			return 0;
		}
	}

	public int getCount() {
		return sorting.size();
	}

	public int get(int index) {
		return sorting.get(index);
	}

	public void toggleSortByNameMemo() {
		toggle(SORT_MEM_ASC);
	}

	public int getSortByNameMemo() {
		return isSet(SORT_MEM_ASC);
	}

	public void toggleSortByCategory() {
		toggle(SORT_CAT_ASC);
	}

	public int getSortByCategory() {
		return isSet(SORT_CAT_ASC);
	}

	public void toggleSortByIsSubscription() {
		toggle(SORT_SUB_ASC);
	}

	public int getSortByIsSubscription() {
		return isSet(SORT_SUB_ASC);
	}

	public void toggleSortByDate() {
		toggle(SORT_DAT_ASC);
	}

	public int getSortByDate() {
		return isSet(SORT_DAT_ASC);
	}

	public void toggleSortByAmount() {
		toggle(SORT_VAL_ASC);
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
			return compareStr(spending1.getAmount(), spending2.getAmount());
		case SORT_VAL_DSC:
			return compareStr(spending2.getAmount(), spending1.getAmount());
		case SORT_DAT_ASC:
			return compareDate(spending1.getProperDate(), spending2.getProperDate());
		case SORT_DAT_DSC:
			return compareDate(spending2.getProperDate(), spending1.getProperDate());
		default:
			return 0;
		}
	}

	private static int compareStr(String str1, String str2) {
		if (TextUtils.isEmpty(str1)) {
			return TextUtils.isEmpty(str2) ? 0 : -1;
		} else {
			if (TextUtils.isEmpty(str2)) {
				return 1;
			}
		}
		return str1.compareTo(str2);
	}

	private static final int compareBool(boolean bool1, boolean bool2) {
		return (bool1 ? 1 : -1) - (bool2 ? 1 : -1);
	}

	private static final int compareDate(Date date1, Date date2) {
		if (date1 == null) {
			return date2 == null ? 0 : -1;
		} else {
			if (date2 == null) {
				return 1;
			}
		}
		return date1.compareTo(date2);
	}
}
