/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o;

import java.util.*;

import com.db4o.ext.*;

/**
 * @exclude 
 */
public class ObjectSetImpl extends AbstractList implements ExtObjectSet{
    
    final QResult _delegate;
    
    ObjectSetImpl(QResult qResult){
        _delegate = qResult;
    }

    public long[] getIDs() {
        return _delegate.getIDs();
    }

    public ExtObjectSet ext() {
        return this;
    }

    public boolean hasNext() {
        return _delegate.hasNext();
    }

    public Object next() {
        return _delegate.next();
    }

    public void reset() {
        _delegate.reset();
    }

    public int size() {
        return _delegate.size();
    }
    
    private Object streamLock(){
        return _delegate.streamLock();
    }
    
    private YapStream stream(){
        return _delegate.i_trans.i_stream;
    }
    
    public boolean contains(Object a_object) {
        return indexOf(a_object) >= 0;
    }

    public Object get(int index) {
        return _delegate.get(index);
    }

    public int indexOf(Object a_object) {
        synchronized(streamLock()){
            int id = (int)stream().getID(a_object);
            if(id <= 0){
                return -1;
            }
            return _delegate.indexOf(id);
        }
    }

    public int lastIndexOf(Object a_object) {
        return indexOf(a_object);
    }
    
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
