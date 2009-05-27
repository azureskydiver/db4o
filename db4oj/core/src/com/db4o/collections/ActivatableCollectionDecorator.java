/* Copyright (C) 2009  Versant Corp.  http://www.db4o.com */

package com.db4o.collections;

import java.util.*;

import com.db4o.activation.*;
import com.db4o.ta.*;

/**
 * @sharpen.ignore
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ActivatableCollectionDecorator<E> implements Collection<E>{
	
	private final Collection<E> _delegate;
	
	private final Activatable _activatable;
	
	ActivatableCollectionDecorator(Collection<E> delegate, Activatable activatable){
		_delegate = delegate;
		_activatable = activatable;
	}

	public boolean add(E e) {
		activateForWrite();
		return _delegate.add(e);
	}

	private void activateForWrite() {
		_activatable.activate(ActivationPurpose.WRITE);
	}

	public boolean addAll(Collection<? extends E> c) {
		activateForWrite();
		return _delegate.addAll(c);
	}

	public void clear() {
		activateForWrite();
		_delegate.clear();
	}

	public boolean contains(Object o) {
		return _delegate.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return _delegate.containsAll(c);
	}

	public boolean isEmpty() {
		return _delegate.isEmpty();
	}

	public Iterator<E> iterator() {
		return new ActivatingIterator<E>(_activatable, _delegate.iterator());
	}

	public boolean remove(Object o) {
		activateForWrite();
		return _delegate.remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		activateForWrite();
		return _delegate.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		activateForWrite();
		return _delegate.retainAll(c);
	}

	public int size() {
		return _delegate.size();
	}

	public Object[] toArray() {
		return _delegate.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return _delegate.toArray(a);
	}

}
