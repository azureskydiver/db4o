/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.collections;

import java.lang.reflect.*;
import java.util.*;

import com.db4o.foundation.*;

/**
 * @exclude
 */
public class Db4oArrayList<E> extends ArrayList<E> {

	private static final long serialVersionUID = 1L;

	public E[] elements;

	public int capacity;

	public int listSize;

	public Db4oArrayList() {
		this(10);
	}

	@SuppressWarnings("unchecked")
	public Db4oArrayList(Collection<? extends E> c) {
		super(c);
		Object[] data = c.toArray();
		capacity = data.length;
		elements = (E[]) new Object[capacity];
		System.arraycopy(data, 0, elements, 0, data.length);
	}

	@SuppressWarnings("unchecked")
	public Db4oArrayList(int initialCapacity) {
		if (initialCapacity < 0) {
			throw new IllegalArgumentException();
		}
		capacity = initialCapacity;
		elements = (E[]) new Object[initialCapacity];
	}

	public void add(int index, E element) {
		checkAddIndex(index);
		ensureCapacity(size() + 1);
		System.arraycopy(elements, index,
				elements, index + 1, listSize - index);
		elements[index] = element;
		increaseSize(1);
	}

	public boolean add(E e) {
		ensureCapacity(size() + 1);
		elements[listSize] = e;
		increaseSize(1);
		return true;
	}

	public boolean addAll(Collection<? extends E> c) {
		int length = c.size();
		if(length == 0) {
			return false;
		}
		ensureCapacity(size() + length);
		Object[] toBeAdded = c.toArray();
		System.arraycopy(toBeAdded, 0, elements, size(), toBeAdded.length);
		increaseSize(length);
		return true;		
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		checkAddIndex(index);
		int length = c.size();
		if(length == 0) {
			return false;
		}
		ensureCapacity(size() + length);
		Object[] data = c.toArray();
		System.arraycopy(elements, index, elements, index+length, size() - index);
		System.arraycopy(data, 0, elements, index, length);
		increaseSize(length);
		return true;
	}

	public void clear() {
		setSize(0);
	}

	public Object clone() {
		throw new NotImplementedException();
	}

	public boolean contains(Object o) {
		return indexOf(o) != -1;
	}

	@SuppressWarnings("unchecked")
	public void ensureCapacity(int minCapacity) {
		super.ensureCapacity(minCapacity);
		if (minCapacity <= capacity) {
			return;
		}
		E[] temp = (E[]) new Object[minCapacity];
		System.arraycopy(elements, 0, temp, 0, capacity);
		elements = temp;
		capacity = minCapacity;
	}

	public E get(int index) {
		checkIndex(index);
		return elements[index];
	}

	public int indexOf(Object o) {
		for (int index = 0; index < size(); ++index) {
			E element = get(index);
			if (o == null ? element == null : o.equals(element)) {
				return index;
			}
		}
		return -1;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public int lastIndexOf(Object o) {
		for (int index = size() - 1; index >= 0; --index) {
			E element = get(index);
			if (o == null ? element == null : o.equals(element)) {
				return index;
			}
		}
		return -1;
	}

	public E remove(int index) {
		checkIndex(index);
		E element = elements[index];
		System.arraycopy(elements, index + 1, 
				elements, index, size() - index	- 1);
		decreaseSize(1);
		return element;
	}

	public boolean remove(Object o) {
		int index = indexOf(o);
		if (index == -1) {
			return false;
		}
		remove(index);
		return true;
	}


	protected void removeRange(int fromIndex, int toIndex) {
		if ((fromIndex < 0 || fromIndex >= size() || toIndex > size() || toIndex < fromIndex)) {
			throw new IndexOutOfBoundsException();
		}
		if (fromIndex == toIndex) {
			return;
		}
		int count = toIndex - fromIndex;
		System.arraycopy(elements, toIndex, elements, fromIndex, size()
				- toIndex);
		listSize -= count;
	}

	public E set(int index, E element) {
		checkIndex(index);
		E oldValue = elements[index];
		elements[index] = element;
		return oldValue;
	}

	public int size() {
		return listSize;
	}

	public Object[] toArray() {
		int size = size();
		Object[] data = new Object[size];
		System.arraycopy(elements, 0, data, 0, size);
		return data;
	}

	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		int size = size();
		if(a.length < size) {
			a = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
		}
		System.arraycopy(elements, 0, a, 0, size);
		return a;
	}

	public void trimToSize() {
		throw new NotImplementedException();
	}

	public boolean equals(Object o) {
		throw new NotImplementedException();
	}

	public int hashCode() {
		throw new NotImplementedException();
	}

	public Iterator<E> iterator() {
		throw new NotImplementedException();
	}

	public ListIterator<E> listIterator() {
		throw new NotImplementedException();
	}

	public ListIterator<E> listIterator(int index) {
		throw new NotImplementedException();
	}

	public List<E> subList(int fromIndex, int toIndex) {
		throw new NotImplementedException();
	}

	public boolean containsAll(Collection<?> c) {
		Iterator<?> iter = c.iterator();
		while(iter.hasNext()) {
			if(!contains(iter.next())) {
				return false;
			}
		}
		return true;
	}

	public boolean removeAll(Collection <?> c) {
		boolean changed = false;
		Iterator<?> it = iterator();
		while (it.hasNext()) {
			if (c.contains(it.next())) {
				it.remove();
				changed = true;
			}
		}
		return changed;
	}

	public boolean retainAll(Collection <?> c) {
		boolean changed = false;
		Iterator<?> it = iterator();
		while (it.hasNext()) {
			if (!c.contains(it.next())) {
				it.remove();
				changed = true;
			}
		}
		return changed;
	}

	public String toString() {
		throw new NotImplementedException();
	}

	private void checkIndex(int index) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException();
		}
	}
	
	private void checkAddIndex(int index) {
		if (index < 0 || index > size()) {
			throw new IndexOutOfBoundsException();
		}
	}
	
	private void setSize(int count) {
		listSize = count;
	}
	
	private void increaseSize(int count) {
		listSize += count;
	}
	
	private void decreaseSize(int count) {
		listSize -= count;
	}


}
