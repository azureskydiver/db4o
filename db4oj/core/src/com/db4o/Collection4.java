/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * Fast linked list for all usecases.
 * 
 * @exclude
 */
public class Collection4 implements DeepClone {

    /** first element of the linked list */
    List4 i_first;

    /** number of elements collected */
    private int i_size;

    /** performance trick only: no object creation */
    private static final EmptyIterator emptyIterator = new EmptyIterator();

    public final void add(Object a_object) {
        i_first = new List4(i_first, a_object);
        i_size++;
    }

	// Not used

    //	final void addAll(Collection4 col) {
    //		Iterator4 i = col.iterator();
    //		while (i.hasNext()) {
    //			add(i.next());
    //		}
    //	}

    final void addAll(Object[] a_objects) {
        if (a_objects != null) {
            for (int i = 0; i < a_objects.length; i++) {
                if (a_objects[i] != null) {
                    add(a_objects[i]);
                }
            }
        }
    }
    
    public final void addAll(Collection4 other){
        if(other != null){
            Iterator4 i = other.iterator();
            while(i.hasNext()){
                add(i.next());
            }
        }
    }

    final void clear() {
        i_first = null;
        i_size = 0;
    }

    public final boolean contains(Object a_obj) {
        return get(a_obj) != null;
    }

    /**
     * tests if the object is in the Collection.
     * == comparison.
     */
    public final boolean containsByIdentity(Object a_obj) {
        List4 current = i_first;
        while (current != null) {
            if (current.i_object != null && current.i_object == a_obj) {
                return true;
            }
            current = current.i_next;
        }
        return false;
    }

    /**
     * returns the first object found in the Collections
     * that equals() the passed object
     */
    final Object get(Object a_obj) {
        Object current;
        Iterator4 i = iterator();
        while (i.hasNext()) {
            current = i.next();
            if (current.equals(a_obj)) {
                return current;
            }
        }
        return null;
    }

    public Object deepClone(Object param) throws CloneNotSupportedException {
        Collection4 col = new Collection4();
        Object element = null;
        Iterator4 i = this.iterator();
        while (i.hasNext()) {
            element = i.next();
            if (element instanceof DeepClone) {
                col.add(((DeepClone) element).deepClone(param));
            } else {
                col.add(element);
            }
        }
        return col;
    }

    /**
     * makes sure the passed object is in the Collection.
     * equals() comparison.
     */
    final Object ensure(Object a_obj) {
        Object obj = get(a_obj);
        if (obj != null) {
            return obj;
        }
        add(a_obj);
        return a_obj;
    }

    public final Iterator4 iterator() {
        if (i_first == null) {
            return emptyIterator;
        }
        return new Iterator4(i_first);
    }

    /**
     * removes an object from the Collection
     * equals() comparison
     * returns the removed object or null, if none found
     */
    public Object remove(Object a_object) {
        List4 previous = null;
        List4 current = i_first;
        while (current != null) {
            if (current.i_object.equals(a_object)) {
                i_size--;
                if (previous == null) {
                    i_first = current.i_next;
                } else {
                    previous.i_next = current.i_next;
                }
                return current.i_object;
            }
            previous = current;
            current = current.i_next;
        }
        return null;
    }

    public final int size() {
        return i_size;
    }

    /**
     * This is a non reflection implementation for more speed.
     * In contrast to the JDK behaviour, the passed array has
     * to be initialized to the right length. 
     */
    final void toArray(Object[] a_array) {
        int j = i_size;
        Iterator4 i = iterator();

        // backwards, since our linked list is the wrong way arround
        while (i.hasNext()) {
            a_array[--j] = i.next();
        }
    }

}
