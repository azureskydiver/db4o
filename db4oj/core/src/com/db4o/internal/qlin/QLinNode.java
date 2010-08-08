/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.qlin;

import com.db4o.qlin.*;

/**
 * @exclude
 */
public abstract class QLinNode <T> implements QLin<T> {
	
	
	public QLin<T> equal(Object obj) {
		throw new QLinException("#equal() is not supported on this node");
	}
	
	public QLin<T> startsWith(String string) {
		throw new QLinException("#startsWith() is not supported on this node");
	}
	
	public QLin<T> smaller(Object obj) {
		throw new QLinException("#smaller() is not supported on this node");
	}
	
	public QLin<T> greater(Object obj) {
		throw new QLinException("#greater() is not supported on this node");
	}

}
