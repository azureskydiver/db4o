/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;


/**
 * @exclude
 */
public class Algorithms4 {
	
	interface QuickSortable4 {
		int size();
		int compare(int leftIndex, int rightIndex);
		void swap(int leftIndex, int rightIndex);
	}
	
	public static void qsort(QuickSortable4 sortable) {
		qsort(sortable, 0, sortable.size()-1);
	}

	private static void qsort(QuickSortable4 sortable, int from, int to) {
		if (to-from<1) {
			return;
		}
		int pivot = to;
		int left = from;
		int right = to;
		while (left<right) {
			while (left<right && sortable.compare(pivot,left)<0) {
				left++;
			}
			while(left<right && sortable.compare(pivot,right)>=0) {
				right--;
			}
			swap(sortable, left, right);
		}
		swap(sortable, to, right);
		qsort(sortable, from, right-1);
		qsort(sortable, right+1, to);
	}

	private static void swap(QuickSortable4 sortable, int left, int right) {
		if (left == right) {
			return;
		}
		sortable.swap(left, right);
	}

}
