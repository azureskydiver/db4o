/* Copyright (C) 2008  db4objects Inc.   http://www.db4o.com */
package com.db4o.foundation;

import java.util.*;

/**
 * @exclude
 */
public class IterableBaseWrapper implements IterableBase {

	private Object _delegate;
	
	public Iterator iterator() {
		return null;
	}

	public Object delegate() {
		return _delegate;
	}
	
}
