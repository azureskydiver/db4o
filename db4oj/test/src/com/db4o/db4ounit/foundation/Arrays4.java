package com.db4o.db4ounit.foundation;

public class Arrays4 {

	public static int[] fill(int[] array, int value) {
		for (int i=0; i<array.length; ++i) {
			array[i] = value;
		}
		return array;
	}

	public static int[] concat(int[] a, int[] b) {
		int[] array = new int[a.length + b.length];
		System.arraycopy(a, 0, array, 0, a.length);
		System.arraycopy(b, 0, array, a.length, b.length);
		return array;
	}

	public static int occurences(int[] values, int value) {
	    int count = 0;
	    for (int i = 0; i < values.length; i++) {
	        if(values[i] == value){
	            count ++;
	        }
	    }
	    return count;
	}

	public static int[] clone(int[] bars) {
		int[] array = new int[bars.length];
		System.arraycopy(bars, 0, array, 0, bars.length);
		return array;
	}

	public static Object[] toObjectArray(int[] values) {
	    Object[] ret = new Object[values.length];
	    for (int i = 0; i < values.length; i++) {
	        ret[i] = new Integer(values[i]);
	    }
	    return ret;
	}

}
