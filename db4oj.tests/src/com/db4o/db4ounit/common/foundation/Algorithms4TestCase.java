/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;

public class Algorithms4TestCase implements TestCase {
	
	public static class QuickSortableIntArray implements QuickSortable4{
		
		private int[] ints;
		
		public QuickSortableIntArray(int[] ints) {
			this.ints = ints;
		}

		public int compare(int leftIndex, int rightIndex) {
			return ints[leftIndex] - ints[rightIndex]; 
		}

		public int size() {
			return ints.length;
		}

		public void swap(int leftIndex, int rightIndex) {
			int temp = ints[leftIndex];
			ints[leftIndex] = ints[rightIndex];
			ints[rightIndex] = temp;
		}
		
		public void assertSorted(){
			for (int i = 0; i < ints.length; i++) {
				Assert.areEqual( i + 1, ints[i]);
			}
		}
	}
	
	public void testUnsorted(){
		int[] ints = new int[]{ 3 , 5, 2 , 1, 4 };
		assertQSort(ints);
	}

	public void testStackUsage(){
		int[] ints = new int[50000];
		for(int i=0;i<ints.length;i++) {
			ints[i]=i+1;
		}
		assertQSort(ints);
	}

	private void assertQSort(int[] ints) {
		QuickSortableIntArray sample = new QuickSortableIntArray(ints);
		Algorithms4.qsort(sample);
		sample.assertSorted();
	}


}
