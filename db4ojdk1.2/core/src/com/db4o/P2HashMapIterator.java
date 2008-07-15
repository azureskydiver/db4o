/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.util.*;

/**
 * @persistent 
 * @deprecated since 7.0
 * @decaf.ignore.jdk11
 */
class P2HashMapIterator implements Iterator {

    private P1HashElement i_current;

    private final P2HashMap i_map;
    private int i_nextIndex;

    private P1HashElement i_previous;

    P2HashMapIterator(P2HashMap a_map) {
        i_map = a_map;
        i_nextIndex = -1;
        getNextCurrent();
    }

    private int currentIndex() {
        if (i_current == null) {
            return -1;
        }
        return i_current.i_hashCode & i_map.i_mask;
    }

    private void getNextCurrent() {
        i_previous = i_current;
        i_current = (P1HashElement)nextElement();
        if (i_current != null) {
            i_current.checkActive();
        }
    }

    public boolean hasNext() {
        synchronized (i_map.streamLock()) {
            return i_current != null;
        }
    }

    public Object next() {
        synchronized (i_map.streamLock()) {
            i_map.checkActive();
            Object ret = null;
            if (i_current != null) {
                ret = i_current.activatedKey(i_map.elementActivationDepth());
            }
            getNextCurrent();
            return ret;
        }
    }

    private P1ListElement nextElement() {
        if (i_current != null && i_current.i_next != null) {
            return i_current.i_next;
        }
        if (i_nextIndex <= currentIndex()) {
            searchNext();
        }
        if (i_nextIndex >= 0) {
            return i_map.i_table[i_nextIndex];
        }
        return null;
    }

    public void remove() {
        synchronized (i_map.streamLock()) {
            i_map.checkActive();
            if (i_previous != null) {
                int index = i_previous.i_hashCode & i_map.i_mask;
                if (index >= 0 && index < i_map.i_table.length) {
                    P1HashElement last = null;
                    P1HashElement phe = i_map.i_table[index];
                    while (phe != i_previous && phe != null) {
                        phe.checkActive();
                        last = phe;
                        phe = (P1HashElement)phe.i_next;
                    }
                    if (phe != null) {
                        i_map.i_size--;
                        if (last == null) {
                            i_map.i_table[index] = (P1HashElement)phe.i_next;
                        } else {
                            last.i_next = phe.i_next;
                            last.update();
                        }
                        i_map.modified();
                        phe.delete(i_map.i_deleteRemoved);
                    }
                }
                i_previous = null;
            }
        }
    }

    private void searchNext() {
        if (i_nextIndex > -2) {
            while (++i_nextIndex < i_map.i_tableSize) {
                if (i_map.i_table[i_nextIndex] != null) {
                    return;
                }
            }
            i_nextIndex = -2;
        }
    }

}
