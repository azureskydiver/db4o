/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.wrap;

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;

/**
 * wraps a db4o ObjectSet to an AbstractList and provides a 
 * java.util.List and java.util.Collection interface to an
 * ObjectSet.<br>
 */
public class ObjectSetCollection extends AbstractList {

    public int activationDepth = 5;

    private ExtObjectContainer i_objectContainer;
    private long[] i_ids;
    
    public ObjectSetCollection(ObjectContainer objectContainer, ObjectSet objectSet) {
        this.i_objectContainer = objectContainer.ext();
        this.i_ids = objectSet.ext().getIDs();
    }

    private Object activatedObject(int a_index) {
        Object obj = i_objectContainer.getByID(i_ids[a_index]);
        i_objectContainer.activate(obj, activationDepth);
        return obj;
    }

    public void add(int i, Object obj) {
        throw new UnsupportedOperationException();
    }

    public boolean add(Object obj) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(int i, Collection c) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean contains(Object a_object) {
        return indexOf(a_object) >= 0;
    }

    public Object get(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException("Index " + index + " not within bounds.");
        }
        return activatedObject(index);
    }

    public int indexOf(Object a_object) {
        long id = i_objectContainer.getID(a_object);
        if (id > 0) {
            for (int i = 0; i < i_ids.length; i++) {
                if (i_ids[i] == id) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int lastIndexOf(Object a_object) {
        return indexOf(a_object);
    }

    public Object remove(int index) {
        throw new UnsupportedOperationException();
    }

    public Object set(int i, Object obj) {
        throw new UnsupportedOperationException();
    }

    public int size() {
        return i_ids.length;
    }

    public Object[] toArray() {
        Object[] arr = new Object[size()];
        toArray1(arr);
        return arr;
    }

    public Object[] toArray(Object arr[]) {
        if (arr.length < size()) {
            arr =
                (Object[])java.lang.reflect.Array.newInstance(
                    arr.getClass().getComponentType(),
                    size());
        }
        toArray1(arr);
        return arr;
    }

    private void toArray1(Object arr[]) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = activatedObject(i);
        }
    }

}
