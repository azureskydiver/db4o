/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.foundation;

import com.db4o.types.*;

/**
 * elements in linked list Collection4
 * 
 * @exclude
 */
public final class List4 implements Unversioned
{
	// TODO: encapsulate field access
	/**
	 * next element in list
	 */
	public List4 _next;
	
	/**
	 * carried object
	 */
	public Object _element;  
	
	/**
	 * db4o constructor to be able to store objects of this class
	 */
	public List4() {}
	
	public List4(Object element) {
		_element = element;
	}

	public List4(List4 next, Object element) {
		_next = next;
		_element = element;
	}

	boolean holds(Object obj) {
		if(obj == null){
			return _element == null;
		}
		return obj.equals(_element);
	}

	public static int size(List4 list) {
		int counter = 0;
		List4 nextList = list; 
		while(nextList != null){
			counter++;
			nextList = nextList._next;
		}
		return counter;
	}
	
}
