/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * @exclude
 * 
 * @renameto com.db4o.inside.Comparable4 
 */
public interface YapComparable {
	
	YapComparable prepareComparison(Object obj);
	
	int compareTo(Object obj);
	
	boolean isEqual(Object obj);
	
	boolean isGreater(Object obj);
	
	boolean isSmaller(Object obj);
    
    Object current();
    
}

