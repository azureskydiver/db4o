package com.db4o.cs.client;

import com.db4o.ObjectSet;
import com.db4o.ext.ExtObjectSet;

import java.util.List;
import java.util.Iterator;
import java.util.Collection;
import java.util.ListIterator;

/**
 * Delegate wrapper for List to ObjectSet
 * User: treeder
 * Date: Nov 25, 2006
 * Time: 3:11:07 PM
 */
public class ObjectSetListWrapper implements ObjectSet, List {
	private List list;

	public ObjectSetListWrapper(List list) {
		this.list = list;
	}

	// todo: implements these ObjectSet and Iterator methods
	public ExtObjectSet ext() {
		return null;
	}

	public boolean hasNext() {
		return false;
	}

	public Object next() {
		return null;
	}

	public void remove() {

	}

	public void reset() {

	}

	public int size() {
		return list.size();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public boolean contains(Object o) {
		return list.contains(o);
	}

	public Iterator iterator() {
		return list.iterator();
	}

	public Object[] toArray() {
		return list.toArray();
	}

	public Object[] toArray(Object[] a) {
		return list.toArray(a);
	}

	public boolean add(Object o) {
		return list.add(o);
	}

	public boolean remove(Object o) {
		return list.remove(o);
	}

	public boolean containsAll(Collection c) {
		return list.containsAll(c);
	}

	public boolean addAll(Collection c) {
		return list.addAll(c);
	}

	public boolean addAll(int index, Collection c) {
		return list.addAll(index, c);
	}

	public boolean removeAll(Collection c) {
		return list.removeAll(c);
	}

	public boolean retainAll(Collection c) {
		return list.retainAll(c);
	}

	public void clear() {
		list.clear();
	}

	public boolean equals(Object o) {
		return list.equals(o);
	}

	public int hashCode() {
		return list.hashCode();
	}

	public Object get(int index) {
		return list.get(index);
	}

	public Object set(int index, Object element) {
		return list.set(index, element);
	}

	public void add(int index, Object element) {
		list.add(index, element);
	}

	public Object remove(int index) {
		return list.remove(index);
	}

	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	public ListIterator listIterator() {
		return list.listIterator();
	}

	public ListIterator listIterator(int index) {
		return list.listIterator(index);
	}

	public List subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}
}
