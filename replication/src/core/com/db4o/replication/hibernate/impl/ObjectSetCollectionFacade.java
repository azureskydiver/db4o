package com.db4o.replication.hibernate.impl;

import com.db4o.foundation.ObjectSetAbstractFacade;

import java.util.Collection;
import java.util.Iterator;

public class ObjectSetCollectionFacade extends ObjectSetAbstractFacade {

	Collection _delegate;
	Iterator _itor;

	public ObjectSetCollectionFacade(Collection delegate_) {
		_delegate = delegate_;
		_itor = _delegate.iterator();
	}

	public boolean hasNext() {
		return _itor.hasNext();
	}

	public Object next() {
		return _itor.next();
	}

	public int size() {
		return _delegate.size();
	}

	public boolean contains(Object o) {
		return _delegate.contains(o);
	}
}
