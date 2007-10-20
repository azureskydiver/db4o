/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta;

/**
 * @exclude
 */
public class LinkedList {

	public LinkedList next;

	public LinkedList() {

	}

	public static LinkedList newList(int depth) {
		if (depth == 0) {
			return null;
		}
		LinkedList head = new LinkedList();
		head.next = newList(depth - 1);
		return head;
	}

	/**
	 * Overrides this method to assert that 
	 * <codee>other</code> is only activated with depth 1.
	 */
	public boolean equals(Object other) {
		return ((LinkedList) other).next == null;
	}
}
