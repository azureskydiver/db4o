/* Copyright (C) 2008  db4objects Inc.   http://www.db4o.com */
package com.db4o.foundation;

import java.util.*;

/**
 * @exclude
 * @decaf.ignore.jdk11
 */
public class IterableBaseFactory {

	public static IterableBase coerce(Object obj) {
		if(obj instanceof Collection) {
			return new CollectionIterableBase((Collection) obj);
		}
		try {
			return new ReflectionIterableBase(obj);
		}
		catch (Exception exc) {
			throw new RuntimeException(exc);
		}
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
