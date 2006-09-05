/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;


/**
 * @exclude
 */
public class Arrays4 {

	public static int indexOf(Object[] array, Object element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}
}
