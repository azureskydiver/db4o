/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.foundation;

import com.db4o.types.Unversioned;

/**
 * Fast linked list for all usecases.
 * 
 * @exclude
 */
public class Collection4 implements Iterable4, DeepClone, Unversioned {
	
	// FIELDS ARE PUBLIC SO THEY CAN BE REFLECTED ON IN JDKs <= 1.1

	/** first element of the linked list */
	public List4 _first;
	
	public List4 _last;

	/** number of elements collected */
	public int _size;

	public int _version;		

	public Collection4() {
	}

	public Collection4(Iterable4 other) {
		addAll(other);
	}

	public Object singleElement() {
		if (size() != 1) {
			throw new IllegalStateException();
		}
		return _first._element;
	}

	/**
	 * Adds an element to the end of this collection.
	 * 
	 * @param element
	 */
	public final void add(Object element) {
		assertNotNull(element);
		doAdd(element);
		changed();
	}	
	
	public final void prepend(Object element) {
		assertNotNull(element);
		doPrepend(element);
		changed();
	}

	private void doPrepend(Object element) {
		if (_first == null) {
			doAdd(element);
		} else {
			_first = new List4(_first, element);
			_size++;
		}
	}

	private void doAdd(Object element) {
		if (_last == null) {
			_first = new List4(element);
			_last = _first;
		} else {
			_last._next = new List4(element);
			_last = _last._next;
		}
		_size++;
	}

	public final void addAll(Object[] elements) {
		assertNotNull(elements);
		for (int i = 0; i < elements.length; i++) {
			add(elements[i]);
		}
	}

	public final void addAll(Iterable4 other) {
		assertNotNull(other);
		addAll(other.iterator());
	}

	public final void addAll(Iterator4 iterator) {
		assertNotNull(iterator);
		while (iterator.moveNext()) {
			add(iterator.current());
		}
	}

	public final void clear() {
		_first = null;
		_last = null;
		_size = 0;
		changed();
	}

	public final boolean contains(Object element) {
		return get(element) != null;
	}

	public boolean containsAll(Iterator4 iter) {
		assertNotNull(iter);
		while (iter.moveNext()) {
			if (!contains(iter.current())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * tests if the object is in the Collection. == comparison.
	 */
	public final boolean containsByIdentity(Object element) {
		if (null == element) {
			return false;
		}
		Iterator4 i = internalIterator();
		while (i.moveNext()) {
			Object current = i.current();
			if (current == element) {
				return true;
			}
		}
		return false;
	}

	/**
	 * returns the first object found in the Collections that equals() the
	 * passed object
	 */
	public final Object get(Object element) {
		assertNotNull(element);
		Iterator4 i = internalIterator();
		while (i.moveNext()) {
			Object current = i.current();
			if (current.equals(element)) {
				return current;
			}
		}
		return null;
	}

	public Object deepClone(Object newParent) {
		Collection4 col = new Collection4();
		Object element = null;
		Iterator4 i = internalIterator();
		while (i.moveNext()) {
			element = i.current();
			if (element instanceof DeepClone) {
				col.add(((DeepClone) element).deepClone(newParent));
			} else {
				col.add(element);
			}
		}
		return col;
	}

	/**
	 * makes sure the passed object is in the Collection. equals() comparison.
	 */
	public final Object ensure(Object element) {
		Object existing = get(element);
		if (existing != null) {
			return existing;
		}
		add(element);
		return element;
	}

	/**
	 * Iterates through the collection in reversed insertion order which happens
	 * to be the fastest.
	 * 
	 * @return
	 */
	public final Iterator4 iterator() {
		return _first == null
			? Iterator4Impl.EMPTY
			: new Collection4Iterator(this, _first);
	}

	/**
	 * removes an object from the Collection equals() comparison returns the
	 * removed object or null, if none found
	 */
	public Object remove(Object a_object) {
		List4 previous = null;
		List4 current = _first;
		while (current != null) {
			if (current._element.equals(a_object)) {
				_size--;
				adjustOnRemoval(previous, current);
				changed();
				return current._element;
			}
			previous = current;
			current = current._next;
		}
		return null;
	}

	private void adjustOnRemoval(List4 previous, List4 removed) {
		if (removed == _first) {
			_first = removed._next;
		} else {
			previous._next = removed._next;
		}
		if (removed == _last) {
			_last = previous;
		}
	}

	public final int size() {
		return _size;
	}

	/**
	 * This is a non reflection implementation for more speed. In contrast to
	 * the JDK behaviour, the passed array has to be initialized to the right
	 * length.
	 */
	public final Object[] toArray(Object[] a_array) {
		int j = 0;
		Iterator4 i = internalIterator();
		while (i.moveNext()) {
			a_array[j++] = i.current();
		}
		return a_array;
	}

	public final Object[] toArray() {
		Object[] array = new Object[_size];
		toArray(array);
		return array;
	}

	public String toString() {
		if (_size == 0) {
			return "[]";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		Iterator4 i = internalIterator();
		i.moveNext();
		sb.append(i.current());
		while (i.moveNext()) {
			sb.append(", ");
			sb.append(i.current());
		}
		sb.append("]");
		return sb.toString();
	}

	private void changed() {
		++_version;
	}

	int version() {
		return _version;
	}

	private void assertNotNull(Object element) {
		if (element == null) {
			throw new ArgumentNullException();
		}
	}
	
	/**
	 * Leaner iterator for faster iteration (but unprotected against
	 * concurrent modifications).
	 */
	private Iterator4 internalIterator() {
		return new Iterator4Impl(_first);
	}
}