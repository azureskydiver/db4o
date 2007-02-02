/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;


/**
 * @exclude
 * 
 * @renameto com.db4o.internal.Comparable4 
 */
public interface Comparable4 {
	
	Comparable4 prepareComparison(Object obj);
	
	int compareTo(Object obj);
	
	boolean isEqual(Object obj);
	
	boolean isGreater(Object obj);
	
	boolean isSmaller(Object obj);
    
    Object current();
    
}

