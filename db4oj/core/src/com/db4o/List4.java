/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * elements in linked list Collection4
 * 
 * @exclude
 */
public final class List4
{
	/**
	 * next element in list
	 */
	List4 i_next;
	
	/**
	 * carried object
	 */
	Object i_object;  
	
	/**
	 * db4o constructor to be able to store objects of this class
	 */
	public List4(){}

	List4(List4 a_next, Object a_object){
		i_next = a_next;
		i_object = a_object;
	}
	

	
}
