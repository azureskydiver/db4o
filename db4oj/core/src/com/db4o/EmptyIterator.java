/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

final class EmptyIterator extends Iterator4 {

	EmptyIterator() {
		super(null);
	}

	public final boolean hasNext() {
		return false;
	}
}