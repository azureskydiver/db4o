/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;

/**
 * @exclude
 */
public class Algorithms4 {

	public static void qsort(QuickSortable4 sortable) {
		qsort(sortable, 0, sortable.size());
	}

	public static void qsort(QuickSortable4 sortable, int start, int end) {
		int length = end - start;
		if (length < 7) {
			insertionSort(sortable, start, end);
			return;
		}
		int middle = start + length / 2;
		if (length > 7) {
			int bottom = start;
			int top = end - 1;
			if (length > 40) {
				length /= 8;
				bottom = middleValueIndex(sortable, bottom, bottom + length, bottom
						+ (2 * length));
				middle = middleValueIndex(sortable, middle - length, middle, middle
						+ length);
				top = middleValueIndex(sortable, top - (2 * length), top - length, top);
			}
			middle = middleValueIndex(sortable, bottom, middle, top);
		}
		int a, b, c, d;
		a = b = start;
		c = d = end - 1;
		while (true) {
			while (b <= c && sortable.compare(b, middle) <= 0) {
				if (sortable.compare(b, middle) == 0) {
					middle = newPartionIndex(middle, a, b);
					swap(sortable, a++, b);
				}
				b++;
			}
			while (c >= b && sortable.compare(c, middle) >= 0) {
				if (sortable.compare(c, middle) == 0) {
					middle = newPartionIndex(middle, c, d);
					swap(sortable, c, d--);
				}
				c--;
			}
			if (b > c) {
				break;
			}
			middle = newPartionIndex(middle, b, c);
			swap(sortable, b++, c--);
		}
		length = Math.min(a - start,b - a); 
		
		swap(sortable, start, b - length, length);
		length = Math.min(d - c, end - 1 - d);

		swap(sortable, b, end - length, length);
		length = b - a;
		if (length > 0) {
			qsort(sortable, start, start + length);
		}
		length = d - c;
		if (length > 0) {
			qsort(sortable, end - length, end);
		}

	}

	private static void insertionSort(QuickSortable4 sortable, int start,
			int end) {
		for (int i = start + 1; i < end; i++) {
			for (int j = i; j > start && sortable.compare(j - 1, j) > 0; j--) {
				swap(sortable, j - 1, j);
			}
		}
	}

	private static int newPartionIndex(int oldPartionIndex, int leftSwapIndex, int rightSwapIndex) {
		if(leftSwapIndex == oldPartionIndex) {
			return rightSwapIndex;
		} else if (rightSwapIndex == oldPartionIndex) {
			return leftSwapIndex;
		}
		return oldPartionIndex;
	}

	private static int middleValueIndex(QuickSortable4 sortable, int a, int b, int c) {
		if (sortable.compare(a, b) < 0) {
			if (sortable.compare(b, c) < 0) {
				return b;
			} else {
				if (sortable.compare(a, c) < 0) {
					return c;
				} else {
					return a;
				}
			}
		} else {
			if (sortable.compare(b, c) > 0) {
				return b;
			} else {
				if (sortable.compare(a, c) > 0) {
					return c;
				} else {
					return a;
				}
			}
		}
	}

	private static void swap(QuickSortable4 sortable, int left, int right) {
		if (left == right) {
			return;
		}
		sortable.swap(left, right);
	}
	
	private static void swap(QuickSortable4 sortable, int from, int to, int length) {
		while (length-- > 0) {
			swap(sortable, from++, to++);
		}
	}
	
}
