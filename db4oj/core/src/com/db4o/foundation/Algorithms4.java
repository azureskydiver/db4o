/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;


/**
 * @exclude
 */
public class Algorithms4 {
	
	private static class Range {
		int _from;
		int _to;

		public Range(int from, int to) {
			_from = from;
			_to = to;
		}
	}
	
	public static void qsort(QuickSortable4 sortable) {
		Stack4 stack=new Stack4();
		addRange(stack, 0, sortable.size()-1);
		qsort(sortable,stack);
	}

	private static void qsort(QuickSortable4 sortable, Stack4 stack) {
		while(!stack.isEmpty()) {
			Range range=(Range)stack.peek();
			stack.pop();
			int from=range._from;
			int to=range._to;
			int pivot = to;
			int left = from;
			int right = to;
			while (left<right) {
				while (left<right && sortable.compare(left,pivot)<0) {
					left++;
				}
				while(left<right && sortable.compare(right,pivot)>=0) {
					right--;
				}
				swap(sortable, left, right);
			}
			swap(sortable, to, right);
			addRange(stack, from, right-1);
			addRange(stack, right+1, to);
		}
	}

	private static void addRange(Stack4 stack,int from,int to) {
		if (to-from < 1) {
			return;
		}
		stack.push(new Range(from,to));
	}
	
	private static void swap(QuickSortable4 sortable, int left, int right) {
		if (left == right) {
			return;
		}
		sortable.swap(left, right);
	}

}
