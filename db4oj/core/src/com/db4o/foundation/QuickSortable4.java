/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;


/**
 * @exclude
 */
public interface QuickSortable4 {
	
	public int size();
	
	public int compare(int leftIndex, int rightIndex);
	
	public void swap(int leftIndex, int rightIndex);

}
