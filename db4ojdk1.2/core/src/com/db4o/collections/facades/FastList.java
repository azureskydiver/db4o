/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.collections.facades;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.internal.collections.*;


/**
 * @exclude
 */
public class FastList implements java.util.List{
    
    public PersistentList _persistentList;
    
    public FastList(PersistentList persistentList) {
    	_persistentList = persistentList;
    }
    
    public boolean add(Object o) {
        return _persistentList.add(o);
    }

    public void add(int index, Object element) {
        _persistentList.add(index, element);
    }

    public boolean addAll(final Collection c) {
        return _persistentList.addAll(new JdkCollectionIterable4(c));
    }

    public boolean addAll(int index, Collection c) {
        return _persistentList.addAll(index, new JdkCollectionIterable4(c));
    }

    public void clear() {
        _persistentList.clear();
    }

    public boolean contains(Object o) {
        return _persistentList.contains(o);
    }

    public boolean containsAll(Collection c) {
        return _persistentList.containsAll(new JdkCollectionIterable4(c));
    }

    public Object get(int index) {
        return _persistentList.get(index);
    }

    public int indexOf(Object o) {
        return _persistentList.indexOf(o);
    }

    public boolean isEmpty() {
        return _persistentList.isEmpty();
    }

    public Iterator iterator() {
        return new Iterator4JdkIterator(_persistentList.iterator());
    }

    public int lastIndexOf(Object o) {
        return _persistentList.lastIndexOf(o);
    }

    public ListIterator listIterator() {
        throw new UnsupportedOperationException();
    }

    public ListIterator listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object o) {
        return _persistentList.remove(o);
    }

    public Object remove(int index) {
        return _persistentList.remove(index);
    }

    public boolean removeAll(Collection c) {
        return _persistentList.removeAll(new JdkCollectionIterable4(c));
    }

    public boolean retainAll(Collection c) {
        return _persistentList.retainAll(new JdkCollectionIterable4(c));
    }

    public Object set(int index, Object element) {
        return _persistentList.set(index, element);
    }

    public int size() {
        return _persistentList.size();
    }

    public List subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    public Object[] toArray() {
        return _persistentList.toArray();
    }

    public Object[] toArray(Object[] a) {
        return _persistentList.toArray(a);
    }

}
