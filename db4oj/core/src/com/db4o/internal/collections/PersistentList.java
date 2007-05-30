/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.collections;

import com.db4o.foundation.*;


/**
 * @exclude
 */
public interface PersistentList {
    
    public boolean add(Object o);

    public void add(int index, Object element);

    public boolean addAll(Iterable4 i);

    public boolean addAll(int index, Iterable4 i);

    public void clear();
    
    public boolean contains(Object o);

    public boolean containsAll(Iterable4 i);

    public Object get(int index);

    public int indexOf(Object o);

    public boolean isEmpty();

    public Iterator4 iterator();

    public int lastIndexOf(Object o);

    public boolean remove(Object o);

    public Object remove(int index);

    public boolean removeAll(Iterable4 i);

    public boolean retainAll(Iterable4 i);

    public Object set(int index, Object element);

    public int size();

    public PersistentList subList(int fromIndex, int toIndex);

    public Object[] toArray();

    public Object[] toArray(Object[] a);

}
