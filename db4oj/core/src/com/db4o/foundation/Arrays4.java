/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;

import java.lang.reflect.*;



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

	/**
	 * @sharpen.ignore
	 */
	public static Object[] merge(Object[] a, Object[] b, Class arrayType) {
		Object[] merged = (Object[])Array.newInstance(arrayType, a.length + b.length);
		System.arraycopy(a, 0, merged, 0, a.length);
		System.arraycopy(b, 0, merged, a.length, b.length);
		return merged;
	}

}
