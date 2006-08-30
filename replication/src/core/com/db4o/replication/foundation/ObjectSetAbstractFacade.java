/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.replication.foundation;

import com.db4o.ObjectSet;
import com.db4o.ext.ExtObjectSet;

import java.util.*;

public abstract class ObjectSetAbstractFacade implements ObjectSet {
	private RuntimeException notSupported() {
		return new UnsupportedOperationException("not supported");
	}

	public ExtObjectSet ext() {
		throw notSupported();
	}

	public boolean isEmpty() {
		throw notSupported();
	}

	public Iterator iterator() {
		throw notSupported();
	}

	public Object[] toArray() {
		throw notSupported();
	}

	public Object[] toArray(Object[] arg0) {
		throw notSupported();
	}

	public boolean add(Object arg0) {
		throw notSupported();
	}

	public boolean remove(Object o) {
		throw notSupported();
	}

	public boolean containsAll(Collection arg0) {
		throw notSupported();
	}

	public boolean addAll(Collection arg0) {
		throw notSupported();
	}

	public boolean addAll(int arg0, Collection arg1) {
		throw notSupported();
	}

	public boolean removeAll(Collection arg0) {
		throw notSupported();
	}

	public boolean retainAll(Collection arg0) {
		throw notSupported();
	}

	public void clear() {
		throw notSupported();
	}

	public Object get(int index) {
		throw notSupported();
	}

	public Object set(int arg0, Object arg1) {
		throw notSupported();
	}

	public void add(int arg0, Object arg1) {
		throw notSupported();
	}

	public Object remove(int index) {
		throw notSupported();
	}

	public int indexOf(Object o) {
		throw notSupported();
	}

	public int lastIndexOf(Object o) {
		throw notSupported();
	}

	public ListIterator listIterator() {
		throw notSupported();
	}

	public ListIterator listIterator(int index) {
		throw notSupported();
	}

	public List subList(int fromIndex, int toIndex) {
		throw notSupported();
	}

	public void remove() {
		throw notSupported();
	}
}
