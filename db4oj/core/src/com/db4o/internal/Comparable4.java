/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.foundation.*;


/**
 * @exclude
 * 
 * 
 */
public interface Comparable4 {
	
	Comparable4 prepareComparison(Object obj);
	
	int compareTo(Object obj);
	
	PreparedComparison newPrepareCompare(Object obj);
	
}

