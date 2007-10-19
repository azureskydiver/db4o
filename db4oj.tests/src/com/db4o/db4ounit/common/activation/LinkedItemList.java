/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.activation;

/**
 * @exclude
 */
public class LinkedItemList {

	public LinkedItemList next;
	
	public LinkedItemList() {

	}

	public static LinkedItemList newList(int depth) {
		if (depth == 0) {
			return null;
		}
		LinkedItemList head = new LinkedItemList();
		head.next = newList(depth - 1);
		return head;
	}
}
