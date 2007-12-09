/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.query.processor;


class Order implements Orderable {
	
	private int i_major;
	private int[] i_minors = new int[8];
	private int minorsSize;
	
	public int compareTo(Object obj) {
		if(obj instanceof Order){
			Order other = (Order)obj;
			int res = i_major - other.i_major;
			if(res != 0){
				return res;
			}
			return compareMinors(other);
		}
		return -1;
	}

	public void hintOrder(int a_order, boolean a_major) {
		if(a_major){
			i_major = a_order;
		}else{
		    appendMinor(a_order);
		}
	}
	
	public boolean hasDuplicates(){
		return true;
	}
	
	public String toString() {
	    String str = "Order " + i_major;
	    for (int i = 0; i < minorsSize; i++) {
	        str = str + " " + i_minors[i];
	    }
		return str;
	}

	public void swapMajorToMinor() {
		insertMinor(i_major);
		i_major = 0;
	}
	
	private void appendMinor(int minor) {
	    ensureMinorsCapacity();
	    i_minors[minorsSize] = minor;
	    minorsSize++;
	}
	
	private void insertMinor(int minor) {
	    ensureMinorsCapacity();
	    System.arraycopy(i_minors, 0, i_minors, 1, minorsSize);
	    i_minors[0] = minor;
	    minorsSize++;
	}
	
	private void ensureMinorsCapacity() {
	    if (minorsSize == i_minors.length) {
	        int[] newMinors = new int[minorsSize * 2];
	        System.arraycopy(i_minors, 0, newMinors, 0, minorsSize);
	        i_minors = newMinors;
	    }
	}
	
	private int compareMinors(Order other) {
	    if (minorsSize != other.minorsSize) {
	        throw new RuntimeException("Unexpected exception: this.minorsSize=" + minorsSize 
	                + ", other.minorsSize=" + other.minorsSize);
	    }
	    
	    int result = 0; 
	    for (int i = 0; i < minorsSize; i++) {
	        if (i_minors[i] == other.i_minors[i]) {
	            continue;
	        } else {
	            return (i_minors[i] - other.i_minors[i]);
	        }
	    }
	    return result;
	}
}

