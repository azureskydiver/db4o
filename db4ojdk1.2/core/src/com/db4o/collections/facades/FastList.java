/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.collections.facades;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.internal.collections.*;


/**
 * @exclude
 */
public class FastList implements java.util.List{
    
    public PersistentList _delegate;

    public boolean add(Object o) {
        return _delegate.add(o);
    }

    public void add(int index, Object element) {
        _delegate.add(index, element);
    }

    public boolean addAll(final Collection c) {
        return _delegate.addAll(new JdkCollectionIterable4(c));
    }

    public boolean addAll(int index, Collection c) {
        return _delegate.addAll(index, new JdkCollectionIterable4(c));
    }

    public void clear() {
        _delegate.clear();
    }

    public boolean contains(Object o) {
        return _delegate.contains(o);
    }

    public boolean containsAll(Collection c) {
        return _delegate.containsAll(new JdkCollectionIterable4(c));
    }

    public Object get(int index) {
        return _delegate.get(index);
    }

    public int indexOf(Object o) {
        return _delegate.indexOf(o);
    }

    public boolean isEmpty() {
        return _delegate.isEmpty();
    }

    public Iterator iterator() {
        return new Iterator4JdkIterator(_delegate.iterator());
    }

    public int lastIndexOf(Object o) {
        return _delegate.lastIndexOf(o);
    }

    public ListIterator listIterator() {
        throw new UnsupportedOperationException();
    }

    public ListIterator listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object o) {
        return _delegate.remove(o);
    }

    public Object remove(int index) {
        return _delegate.remove(index);
    }

    public boolean removeAll(Collection c) {
        return _delegate.removeAll(new JdkCollectionIterable4(c));
    }

    public boolean retainAll(Collection c) {
        return _delegate.retainAll(new JdkCollectionIterable4(c));
    }

    public Object set(int index, Object element) {
        return _delegate.set(index, element);
    }

    public int size() {
        return _delegate.size();
    }

    public List subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    public Object[] toArray() {
        return _delegate.toArray();
    }

    public Object[] toArray(Object[] a) {
        return _delegate.toArray(a);
    }

}
