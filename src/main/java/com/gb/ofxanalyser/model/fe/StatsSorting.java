package com.gb.ofxanalyser.model.fe;

import com.gb.ofxanalyser.model.be.CategoryStats;
import com.gb.ofxanalyser.model.fe.base.Sorting;

public class StatsSorting extends Sorting {

	public static final int CRIT_CAT_ASC = 1;
	public static final int CRIT_AVG_ASC = 2;
	public static final int CRIT_M1_ASC = 3;
	public static final int CRIT_M2_ASC = 4;
	public static final int CRIT_M3_ASC = 5;
	public static final int CRIT_M4_ASC = 6;
	public static final int CRIT_M5_ASC = 7;

	public static final int CRIT_CAT_DSC = -1;
	public static final int CRIT_AVG_DSC = -2;
	public static final int CRIT_M1_DSC = -3;
	public static final int CRIT_M2_DSC = -4;
	public static final int CRIT_M3_DSC = -5;
	public static final int CRIT_M4_DSC = -6;
	public static final int CRIT_M5_DSC = -7;

	// increase the capacity if you add more sorting options
	private static final int SORT_CRITERIA_COUNT = 7;

	public StatsSorting() {
		super(SORT_CRITERIA_COUNT);
	}

	public StatsSorting(Sorting copyFrom) {
		super(copyFrom);
	}

	public int getSortByCategory() {
		return isSet(CRIT_CAT_ASC);
	}

	public int getSortByAverage() {
		return isSet(CRIT_AVG_ASC);
	}

	public int getSortByMonth1() {
		return isSet(CRIT_M1_ASC);
	}

	public int getSortByMonth2() {
		return isSet(CRIT_M2_ASC);
	}

	public int getSortByMonth3() {
		return isSet(CRIT_M3_ASC);
	}

	public int getSortByMonth4() {
		return isSet(CRIT_M4_ASC);
	}

	public int getSortByMonth5() {
		return isSet(CRIT_M5_ASC);
	}

	public static int compare(int criteria, CategoryStats stats1, CategoryStats stats2) {
		switch (criteria) {
		case CRIT_CAT_ASC:
			return compare(stats1.getCategory(), stats2.getCategory());
		case CRIT_CAT_DSC:
			return compare(stats2.getCategory(), stats1.getCategory());
		case CRIT_AVG_ASC:
			return compare(stats1.getMonthlyAvg(), stats2.getMonthlyAvg());
		case CRIT_AVG_DSC:
			return compare(stats2.getMonthlyAvg(), stats1.getMonthlyAvg());
		case CRIT_M1_ASC:
			return compare(stats1.getMonths()[0], stats2.getMonths()[0]);
		case CRIT_M1_DSC:
			return compare(stats2.getMonths()[0], stats1.getMonths()[0]);
		case CRIT_M2_ASC:
			return compare(stats1.getMonths()[1], stats2.getMonths()[1]);
		case CRIT_M2_DSC:
			return compare(stats2.getMonths()[1], stats1.getMonths()[1]);
		case CRIT_M3_ASC:
			return compare(stats1.getMonths()[2], stats2.getMonths()[2]);
		case CRIT_M3_DSC:
			return compare(stats2.getMonths()[2], stats1.getMonths()[2]);
		case CRIT_M4_ASC:
			return compare(stats1.getMonths()[3], stats2.getMonths()[3]);
		case CRIT_M4_DSC:
			return compare(stats2.getMonths()[3], stats1.getMonths()[3]);
		case CRIT_M5_ASC:
			return compare(stats1.getMonths()[4], stats2.getMonths()[4]);
		case CRIT_M5_DSC:
			return compare(stats2.getMonths()[4], stats1.getMonths()[4]);
		default:
			return 0;
		}
	}
}
