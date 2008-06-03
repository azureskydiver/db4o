/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.util.*;

/**
 * JDK 2 Iterator
 * 
 * Intended state: i_next is always active.
 *  
 * @persistent
 * @deprecated since 7.0
 * @decaf.ignore
 */
class P2ListElementIterator implements Iterator {

    private final P2LinkedList i_list;

    private P1ListElement i_preprevious;
    private P1ListElement i_previous;
    private P1ListElement i_next;

    P2ListElementIterator(P2LinkedList a_list, P1ListElement a_next) {
        i_list = a_list;
        i_next = a_next;
        checkNextActive();
    }

    private void checkNextActive() {
        if (i_next != null) {
            i_next.checkActive();
        }
    }

    public void remove() {
        if (i_previous != null) {
            synchronized (i_previous.streamLock()) {
                if (i_preprevious != null) {
                    i_preprevious.i_next = i_previous.i_next;
                    i_preprevious.update();
                }
                i_list.checkRemoved(i_preprevious, i_previous);
                i_previous.delete(i_list.i_deleteRemoved);
            }
        }
    }

    public boolean hasNext() {
        return i_next != null;
    }

    public Object next() {
        if (i_next != null) {
            synchronized (i_next.streamLock()) {
                i_preprevious = i_previous;
                i_previous = i_next;
                Object obj = i_next.activatedObject(i_list.elementActivationDepth());
                i_next = i_next.i_next;
                checkNextActive();
                return obj;
            }
        }
        return null;
    }

    P1ListElement nextElement() {
        i_preprevious = i_previous;
        i_previous = i_next;
        i_next = i_next.i_next;
        checkNextActive();
        return i_previous;
    }

    P1ListElement move(int a_elements) {
        if (a_elements < 0) {
            return null;
        }
        for (int i = 0; i < a_elements; i++) {
            if (hasNext()) {
                nextElement();
            } else {
                return null;
            }
        }
        if (hasNext()) {
            return nextElement();
        }
        return null;
    }

}
