/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.query.processor;

import com.db4o.foundation.*;

class Order implements Orderable {
	
	private int i_major;
	private IntArrayList i_minors = new IntArrayList();
	
	public int compareTo(Object obj) {
		if(obj instanceof Order){
			Order other = (Order)obj;
			int res = i_major - other.i_major;
			if(res != 0){
				return res;
			}
			return compareMinors(other.i_minors);
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
	    for (int i = 0; i < i_minors.size(); i++) {
	        str = str + " " + i_minors.get(i);
	    }
		return str;
	}

	public void swapMajorToMinor() {
		insertMinor(i_major);
		i_major = 0;
	}
	
	private void appendMinor(int minor) {
	    i_minors.add(minor);
	}
	
	private void insertMinor(int minor) {
	    i_minors.add(0, minor);
	}
	
	private int compareMinors(IntArrayList other) {
	    if (i_minors.size() != other.size()) {
	        throw new RuntimeException("Unexpected exception: this..size()=" + i_minors.size()
	                + ", other.size()=" + other.size());
	    }
	    
	    int result = 0; 
	    for (int i = 0; i < i_minors.size(); i++) {
	        if (i_minors.get(i) == other.get(i)) {
	            continue;
	        } 
	        return (i_minors.get(i) - other.get(i));
	    }
	    return result;
	}
}

