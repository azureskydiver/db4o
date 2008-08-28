/* Copyright (C) 2008  db4objects Inc.   http://www.db4o.com */
package com.db4o.foundation;

import java.util.*;

/**
 * @exclude
 * @decaf.ignore.jdk11
 * @sharpen.ignore
 */
public class CollectionIterableBase implements IterableBaseWrapper {

	private Collection _delegate;
	
	public CollectionIterableBase(Collection delegate) {
		_delegate = delegate;
	}
	
	public Iterator iterator() {
		return _delegate.iterator();
	}

	public Object delegate() {
		return _delegate;
	}

}
