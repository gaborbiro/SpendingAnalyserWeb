package com.gb.ofxanalyser.model.fe;

import com.gb.ofxanalyser.model.fe.base.Sorting;

public class HistorySorting extends Sorting {

	public static final int CRIT_MEM_ASC = 1;
	public static final int CRIT_CAT_ASC = 2;
	public static final int CRIT_SUB_ASC = 3;
	public static final int CRIT_DAT_ASC = 4;
	public static final int CRIT_VAL_ASC = 5;

	public static final int CRIT_MEM_DSC = -1;
	public static final int CRIT_CAT_DSC = -2;
	public static final int CRIT_SUB_DSC = -3;
	public static final int CRIT_DAT_DSC = -4;
	public static final int CRIT_VAL_DSC = -5;

	// increase the capacity if you add more sorting options
	private static final int SORT_CRITERIA_COUNT = 5;

	public static HistorySorting getSortingByDate() {
		HistorySorting sorting = new HistorySorting();
		sorting.toggle(CRIT_DAT_DSC, true);
		return sorting;
	}

	public static HistorySorting getSortingByDescription() {
		HistorySorting sorting = new HistorySorting();
		sorting.toggle(CRIT_MEM_ASC, true);
		return sorting;
	}

	public HistorySorting() {
		super(SORT_CRITERIA_COUNT);
	}

	public HistorySorting(Sorting copyFrom) {
		super(copyFrom);

		if (copyFrom.getMax() != SORT_CRITERIA_COUNT) {
			throw new IllegalArgumentException("Wrong Sorting item to copy from!");
		}
	}

	public int getSortByNameMemo() {
		return isSet(CRIT_MEM_ASC);
	}

	public int getSortByCategory() {
		return isSet(CRIT_CAT_ASC);
	}

	public int getSortByIsSubscription() {
		return isSet(CRIT_SUB_ASC);
	}

	public int getSortByDate() {
		return isSet(CRIT_DAT_ASC);
	}

	public int getSortByAmount() {
		return isSet(CRIT_VAL_ASC);
	}

	public static int compare(int criteria, TransactionFE transaction1, TransactionFE transaction2) {
		switch (criteria) {
		case CRIT_MEM_ASC:
			return compare(transaction1.getDescription(), transaction2.getDescription());
		case CRIT_MEM_DSC:
			return compare(transaction2.getDescription(), transaction1.getDescription());
		case CRIT_CAT_ASC:
			return compare(transaction1.getCategory(), transaction2.getCategory());
		case CRIT_CAT_DSC:
			return compare(transaction2.getCategory(), transaction1.getCategory());
		case CRIT_SUB_ASC:
			return compare(transaction1.isSubscription(), transaction2.isSubscription());
		case CRIT_SUB_DSC:
			return compare(transaction2.isSubscription(), transaction1.isSubscription());
		case CRIT_VAL_ASC:
			return compare(transaction1.getAmount(), transaction2.getAmount());
		case CRIT_VAL_DSC:
			return compare(transaction2.getAmount(), transaction1.getAmount());
		case CRIT_DAT_ASC:
			return compare(transaction1.getDate(), transaction2.getDate());
		case CRIT_DAT_DSC:
			return compare(transaction2.getDate(), transaction1.getDate());
		default:
			return 0;
		}
	}
}
