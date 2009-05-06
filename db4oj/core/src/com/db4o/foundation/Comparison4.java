/* Copyright (C) 2006 Versant Inc. http://www.db4o.com */

package com.db4o.foundation;

/**
 * @exclude
 */
public interface Comparison4 {
	/**
	 * Returns negative number if x < y
	 * Returns zero if x == y
	 * Returns positive number if x > y
	 */
	int compare(Object x, Object y);
}
