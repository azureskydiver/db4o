/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.test2;

import java.util.*;

/**
 * @decaf.ignore.jdk11
 */
public class CustomComparator implements Comparator {

	public int compare(Object a_obj, Object a_obj2) {
		return ((Comparable)a_obj).compareTo(a_obj2);
	}
}