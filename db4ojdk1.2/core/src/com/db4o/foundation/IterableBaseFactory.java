/* Copyright (C) 2008  db4objects Inc.   http://www.db4o.com */
package com.db4o.foundation;

/**
 * @exclude
 */
public class IterableBaseFactory {

	public static IterableBase coerce(Object obj) {
		return null;
	}
	
	public static Object unwrap(IterableBase iterable) {
		if(iterable instanceof IterableBaseWrapper) {
			return ((IterableBaseWrapper)iterable).delegate();
		}
		return iterable;
	}
	
	private IterableBaseFactory() {
	}

}
