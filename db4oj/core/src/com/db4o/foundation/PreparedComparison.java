/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;

/**
 * TODO: rename to Comparable4 as soon we find
 * a smart name for the current Comparable4.
 */
public interface PreparedComparison {
	
	/**
	 * return a negative int, zero or a positive int if
	 * the object being held in 'this' is smaller, equal 
	 * or greater than the passed object.<br><br>
	 * 
	 * Typical implementation: return this.object - obj;
	 */
	public int compareTo(Object obj);

}
