/* Copyright (C) 2004 - 2006 db4objects Inc.   http://www.db4o.com */
package com.db4o.query;

import java.util.*;

/**
 * @exclude
 * @decaf.ignore.jdk11
 * @sharpen.ignore
 */
public class JdkComparatorWrapper implements QueryComparator {
	private Comparator _comparator;
	
	public JdkComparatorWrapper(Comparator comparator) {
		this._comparator = comparator;
	}

	public int compare(Object first, Object second) {
		return _comparator.compare(first, second);
	}
}
