/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;



/**
 * @exclude
 */
public class Arrays4 {

	public static int indexOfIdentity(Object[] array, Object element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOfEquals(Object[] array, Object expected) {
	    for (int i = 0; i < array.length; ++i) {                
	        if (expected.equals(array[i])) {
	            return i;
	        }
	    }
	    return -1;
	}

	public static boolean areEqual(final byte[] x, final byte[] y) {
		if (x == y) {
			return true;
		}
	    if (x == null) {
	    	return false;
	    }
	    if (x.length != y.length) {
	    	return false;
	    }
	    for (int i = 0; i < x.length; i++) {
			if (y[i] != x[i]) {
				return false;
			}
		}
		return true;
	}

	public static boolean containsInstanceOf(Object[] array, Class klass) {
		if (array == null) {
			return false;
		}
		for (int i=0; i<array.length; ++i) {
			if (klass.isInstance(array[i])) {
				return true;
			}
		}
		return false;
	}

	public static <T> void fill(T[] array, T value) {
		for (int i=0; i<array.length; ++i) {
			array[i] = value;
		}
    }
}
