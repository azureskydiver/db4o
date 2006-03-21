package com.db4o.replication.hibernate.impl;

import com.db4o.foundation.ObjectSetAbstractFacade;

import java.util.Collection;
import java.util.Iterator;

final class ObjectSetCollectionFacade extends ObjectSetAbstractFacade {
// ------------------------------ FIELDS ------------------------------

	private final Collection _delegate;
	private final Iterator _itor;

// --------------------------- CONSTRUCTORS ---------------------------

	public ObjectSetCollectionFacade(Collection delegate_) {
		_delegate = delegate_;
		_itor = _delegate.iterator();
	}

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface List ---------------------

	public final boolean contains(Object o) {
		return _delegate.contains(o);
	}

// --------------------- Interface ObjectSet ---------------------

	public final boolean hasNext() {
		return _itor.hasNext();
	}

	public final Object next() {
		return _itor.next();
	}

	public final int size() {
		return _delegate.size();
	}
}
