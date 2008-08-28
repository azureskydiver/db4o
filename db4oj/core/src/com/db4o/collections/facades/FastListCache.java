/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.collections.facades;

import java.util.*;

/**
 * @exclude 
 * @decaf.ignore.jdk11
 * @sharpen.ignore
 */
public class FastListCache {

	private transient List _list;

	public FastListCache(int size) {
		_list = new ArrayList(size);
		for (int i = 0; i < _list.size(); ++i) {
			_list.set(i, CachedObject.NONE);
		}
	}

	public void add(Object o) {
		_list.add(new CachedObject(o));
	}

	public void add(int index, Object element) {
		_list.add(index, new CachedObject(element));
	}

	public void addAll(Collection c) {
		_list.addAll(toCachedObjectCollection(c));
	}
	
	public void addAll(int index, Collection c) {
		_list.addAll(index, toCachedObjectCollection(c));
	}
	
	public void clear() {
		_list.clear();
	}

	public boolean contains(Object o) {
		return _list.contains(new CachedObject(o));
	}

	public int indexOf(Object o) {
		return _list.indexOf(new CachedObject(o));
	}
	
	public void remove(Object o) {
		_list.remove(new CachedObject(o));	
	}
	
	public void remove(int index) {
		_list.remove(index);
	}

	public void removeAll(Collection c) {
		_list.removeAll(toCachedObjectCollection(c));
	}

	public void retainAll(Collection c) {
		_list.retainAll(toCachedObjectCollection(c));
	}

	public void set(int index, Object element) {
		_list.set(index, new CachedObject(element));
	}
	
	private Collection toCachedObjectCollection(Collection c) {
		ArrayList cachedObjectList = new ArrayList(c.size());
		Iterator iter = c.iterator();
		while(iter.hasNext()) {
			cachedObjectList.add(new CachedObject(iter.next()));
		}
		return cachedObjectList;
	}

	public CachedObject get(int index) {
		return (CachedObject) _list.get(index);
	}

	public boolean containsAll(Collection c) {
		return _list.containsAll(toCachedObjectCollection(c));
	}

}
