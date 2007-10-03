/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.collection;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * @exclude
 */
public class ArrayList4<E> extends AbstractList4<E> implements Cloneable,
		Serializable, RandomAccess{

	private static final long serialVersionUID = 1L;

	public E[] elements;

	public int capacity;

	public int listSize;
	
	public ArrayList4() {
		this(10);
	}

	@SuppressWarnings("unchecked")
	public ArrayList4(Collection<? extends E> c) {
		Object[] data = c.toArray();
		capacity = data.length;
		elements = (E[]) new Object[capacity];
		listSize = data.length;
		System.arraycopy(data, 0, elements, 0, data.length);
	}

	@SuppressWarnings("unchecked")
	public ArrayList4(int initialCapacity) {
		if (initialCapacity < 0) {
			throw new IllegalArgumentException();
		}
		capacity = initialCapacity;
		elements = (E[]) new Object[initialCapacity];
	}

	public boolean add(E e) {
		ensureCapacity(size() + 1);
		elements[listSize] = e;
		increaseSize(1);
		markModified();
		return true;
	}
	
	public void add(int index, E element) {
		checkIndex(index, 0, size());
		ensureCapacity(size() + 1);
		System.arraycopy(elements, index,
				elements, index + 1, listSize - index);
		elements[index] = element;
		increaseSize(1);
		markModified();
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
		markModified();
		return true;		
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		checkIndex(index, 0, size());
		int length = c.size();
		if(length == 0) {
			return false;
		}
		ensureCapacity(size() + length);
		Object[] data = c.toArray();
		System.arraycopy(elements, index, elements, index+length, size() - index);
		System.arraycopy(data, 0, elements, index, length);
		increaseSize(length);
		markModified();
		return true;
	}

	public void clear() {
		setSize(0);
		markModified();
	}
	
	@SuppressWarnings("unchecked")
	public Object clone() {
		ArrayList4<E> clonedList;
		try {
			clonedList = (ArrayList4<E>) super.clone();
			clonedList.elements = elements.clone();
			return clonedList;
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}

	public void ensureCapacity(int minCapacity) {
		if (minCapacity <= capacity) {
			return;
		}
		resize(minCapacity);
	}

	public E get(int index) {
		checkIndex(index, 0, size() - 1);
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
		checkIndex(index, 0, size() - 1);
		E element = elements[index];
		System.arraycopy(elements, index + 1, 
				elements, index, size() - index	- 1);
		decreaseSize(1);
		markModified();
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
		decreaseSize(count);
		markModified();
	}
	
	public E set(int index, E element) {
		checkIndex(index, 0, size() - 1);
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
		resize(size());
	}


	@SuppressWarnings("unchecked")
	private void resize(int minCapacity) {
		markModified();
		E[] temp = (E[]) new Object[minCapacity];
		System.arraycopy(elements, 0, temp, 0, size());
		elements = temp;
		capacity = minCapacity;
	}

	void setSize(int count) {
		listSize = count;
	}
	
	void increaseSize(int count) {
		listSize += count;
	}
	
	void decreaseSize(int count) {
		listSize -= count;
	}
	
	void markModified() {
		++modCount;
	}
}
