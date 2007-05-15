/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.ta.tests.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.db4o.activation.Activator;
import com.db4o.ta.Activatable;
import com.db4o.ta.tests.collections.internal.PagedBackingStore;

/**
 * Platform specific façade.
 * 
 * @param 
 * 
 * @sharpen.ignore
 */
public class PagedList implements List, /* TA BEGIN */ Activatable /* TA END */ {
		
	PagedBackingStore _store = new PagedBackingStore();
	
	// TA BEGIN
	transient Activator _activator;
	// TA END

	public PagedList() {

	}

	public boolean add(Object item) {
		// TA BEGIN
		activate();
		// TA END
		return _store.add(item);
	}
	
	public Object get(int index) {
		// TA BEGIN
		activate();
		// TA END
		return _store.get(index);
	}

	
	public int size() {
		// TA BEGIN
		activate();
		// TA END
		return _store.size();
	}

	public void add(int index, Object element) {
		throw new com.db4o.foundation.NotImplementedException();
	}

	public boolean addAll(Collection c) {
		throw new com.db4o.foundation.NotImplementedException();
	}

	public boolean addAll(int index, Collection c) {
		throw new com.db4o.foundation.NotImplementedException();
	}

	public void clear() {
		throw new com.db4o.foundation.NotImplementedException();
	}

	public boolean contains(Object o) {
		throw new com.db4o.foundation.NotImplementedException();
	}

	public boolean containsAll(Collection c) {
		throw new com.db4o.foundation.NotImplementedException();
	}

	public int indexOf(Object o) {
		throw new com.db4o.foundation.NotImplementedException();
	}

	public boolean isEmpty() {
		throw new com.db4o.foundation.NotImplementedException();
	}

	public Iterator iterator() {
		// TA BEGIN
		activate();
		// TA END
		return new SimpleListIterator(this);
	}

	public int lastIndexOf(Object o) {
		throw new com.db4o.foundation.NotImplementedException();
	}

	public ListIterator listIterator() {
		throw new com.db4o.foundation.NotImplementedException();
	}

	public ListIterator listIterator(int index) {
		throw new com.db4o.foundation.NotImplementedException();
	}

	public boolean remove(Object o) {
		throw new com.db4o.foundation.NotImplementedException();
	}

	public Object remove(int index) {
		throw new com.db4o.foundation.NotImplementedException();
	}

	public boolean removeAll(Collection c) {
		throw new com.db4o.foundation.NotImplementedException();
	}

	public boolean retainAll(Collection c) {
		throw new com.db4o.foundation.NotImplementedException();
	}

	public Object set(int index, Object element) {
		throw new com.db4o.foundation.NotImplementedException();
	}

	public List subList(int fromIndex, int toIndex) {
		throw new com.db4o.foundation.NotImplementedException();
	}

	public Object[] toArray() {
		throw new com.db4o.foundation.NotImplementedException();
	}

	public Object[] toArray(Object[] a) {
		throw new com.db4o.foundation.NotImplementedException();
	}

	// TA BEGIN
	public void bind(Activator activator) {
		if (null != _activator) {
			throw new IllegalStateException();
		}
		_activator = activator;
	}
	
	private void activate() {
		if (_activator == null) return;
		_activator.activate();
	}
	// TA END
}