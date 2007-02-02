/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.util.*;

import com.db4o.internal.*;
import com.db4o.reflect.*;

/**
 * @persistent
 */
class P2HashMapKeySet implements Set {

    private final P2HashMap i_map;

    P2HashMapKeySet(P2HashMap a_map) {
        i_map = a_map;
    }

    public boolean add(Object o) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        i_map.clear();
    }

    public boolean contains(Object o) {
        return i_map.containsKey(o);
    }

    public boolean containsAll(Collection c) {
        synchronized (i_map.streamLock()) {
            i_map.checkActive();
            Iterator i = c.iterator();
            while (i.hasNext()) {
                if (i_map.get4(i.next()) == null) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isEmpty() {
        return i_map.isEmpty();
    }

    public Iterator iterator() {
        synchronized (i_map.streamLock()) {
            i_map.checkActive();
            return new P2HashMapIterator(i_map);
        }
    }

    public boolean remove(Object o) {
        return i_map.remove(o) != null;
    }

    public boolean removeAll(Collection c) {
        synchronized (i_map.streamLock()) {
            i_map.checkActive();
            boolean ret = false;
            Iterator i = c.iterator();
            while (i.hasNext()) {
                if (i_map.remove4(i.next()) != null) {
                    ret = true;
                }
            }
            return ret;
        }
    }

    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    public int size() {
        return i_map.size();
    }

    public Object[] toArray() {
        synchronized (i_map.streamLock()) {
            i_map.checkActive();
            Object[] arr = new Object[i_map.i_size];
            int j = 0;
            Iterator i = new P2HashMapIterator(i_map);
            while (i.hasNext()) {
                arr[j++] = i.next();
            }
            return arr;
        }
    }

    public Object[] toArray(Object[] a) {
        synchronized (i_map.streamLock()) {
            i_map.checkActive();
            int size = i_map.i_size;
            if (a.length < size) {
                Transaction trans = i_map.getTrans();
                if(trans == null){
                    Exceptions4.throwRuntimeException(29);
                }
                Reflector reflector = trans.reflector();
                a =
                    (Object[])reflector.array().newInstance(
                        reflector.forObject(a).getComponentType(),
                        size);
            }
            int j = 0;
            Iterator i = new P2HashMapIterator(i_map);
            while (i.hasNext()) {
                a[j++] = i.next();
            }
            if (a.length > size) {
                a[size] = null;
            }
            return a;
        }
    }

}
