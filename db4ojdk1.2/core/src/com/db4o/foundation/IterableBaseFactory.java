/* Copyright (C) 2008  db4objects Inc.   http://www.db4o.com */
package com.db4o.foundation;

/**
 * @exclude
 * @decaf.ignore.jdk11
 */
public class IterableBaseFactory {

	public static IterableBase coerce(Object obj) {
		return new IterableBaseWrapper(obj);
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
