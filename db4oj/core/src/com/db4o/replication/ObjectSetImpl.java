package com.db4o.replication;

import com.db4o.ObjectSet;
import com.db4o.ext.ExtObjectSet;
import com.db4o.foundation.Iterator4;

public class ObjectSetImpl implements ObjectSet {
	Iterator4 delegate;

	public ObjectSetImpl(Iterator4 delegate) {
		this.delegate = delegate;
	}

	public boolean hasNext() {
		return delegate.hasNext();
	}

	public Object next() {
		return delegate.next();
	}

	public ExtObjectSet ext() {
		throw new UnsupportedOperationException("not supported");
	}

	public void reset() {
		throw new UnsupportedOperationException("not supported");
	}

	public int size() {
		throw new UnsupportedOperationException("not supported");
	}
}
