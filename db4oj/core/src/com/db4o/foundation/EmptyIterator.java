/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.foundation;


final class EmptyIterator implements Iterator4 {

	EmptyIterator() {
	}

	public final boolean moveNext() {
		return false;
	}

	public Object current() {
		throw new IllegalStateException();
	}
}