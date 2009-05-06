/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.internal.query.processor;


interface Orderable {
	
	int compareTo(Object obj);
	void hintOrder(int a_order, boolean a_major);
	boolean hasDuplicates();
	
}

