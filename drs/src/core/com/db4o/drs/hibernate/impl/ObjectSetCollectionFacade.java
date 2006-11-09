package com.db4o.drs.hibernate.impl;

import com.db4o.drs.foundation.ObjectSetAbstractFacade;

import java.util.Collection;
import java.util.Iterator;

final class ObjectSetCollectionFacade extends ObjectSetAbstractFacade {
	private final Collection _delegate;

	private Iterator _itor;

	public ObjectSetCollectionFacade(Collection delegate_) {
		_delegate = delegate_;
		reset();
	}

	public final boolean contains(Object o) {
		return _delegate.contains(o);
	}

	public Collection getDelegate() {
		return _delegate;
	}

	public final boolean hasNext() {
		return _itor.hasNext();
	}

	public final Object next() {
		return _itor.next();
	}

	public void reset() {
		_itor = _delegate.iterator();
	}

	public final int size() {
		return _delegate.size();
	}
	
	public Iterator iterator() {
		return _itor;
	}
}
