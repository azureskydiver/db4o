/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.collections;

import java.util.*;

/**
 * @exclude
 */
public class Db4oArrayList <E> extends ArrayList <E> {

	private static final long serialVersionUID = 1L;

	public Db4oArrayList() {
		super();
	}

	public Db4oArrayList(Collection <? extends E> c) {
		super(c);
	}

	public Db4oArrayList(int initialCapacity) {
		super(initialCapacity);
	}

	@Override
	public void add(int index, E element) {
		// TODO Auto-generated method stub
		super.add(index, element);
	}

	@Override
	public boolean add(E e) {
		// TODO Auto-generated method stub
		return super.add(e);
	}

	@Override
	public boolean addAll(Collection <? extends E>c) {
		// TODO Auto-generated method stub
		return super.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection <? extends E> c) {
		// TODO Auto-generated method stub
		return super.addAll(index, c);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		super.clear();
	}

	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return super.contains(o);
	}

	@Override
	public void ensureCapacity(int minCapacity) {
		// TODO Auto-generated method stub
		super.ensureCapacity(minCapacity);
	}

	@Override
	public E get(int index) {
		// TODO Auto-generated method stub
		return super.get(index);
	}

	@Override
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return super.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return super.isEmpty();
	}

	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return super.lastIndexOf(o);
	}

	@Override
	public E remove(int index) {
		// TODO Auto-generated method stub
		return super.remove(index);
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return super.remove(o);
	}

	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		super.removeRange(fromIndex, toIndex);
	}

	@Override
	public E set(int index, E element) {
		// TODO Auto-generated method stub
		return super.set(index, element);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return super.size();
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return super.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return super.toArray(a);
	}

	@Override
	public void trimToSize() {
		// TODO Auto-generated method stub
		super.trimToSize();
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public Iterator <E> iterator() {
		// TODO Auto-generated method stub
		return super.iterator();
	}

	@Override
	public ListIterator <E> listIterator() {
		// TODO Auto-generated method stub
		return super.listIterator();
	}

	@Override
	public ListIterator <E> listIterator(int index) {
		// TODO Auto-generated method stub
		return super.listIterator(index);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return super.subList(fromIndex, toIndex);
	}

	@Override
	public boolean containsAll(Collection c) {
		// TODO Auto-generated method stub
		return super.containsAll(c);
	}

	@Override
	public boolean removeAll(Collection c) {
		// TODO Auto-generated method stub
		return super.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection c) {
		// TODO Auto-generated method stub
		return super.retainAll(c);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}
