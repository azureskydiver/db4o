/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.*;
import com.db4o.foundation.ArgumentNullException;
import com.db4o.inside.ix.*;


/**
 * @exclude
 */
public class FieldIndexKeyHandler implements Indexable4{
    
    private final YInt _integerHandler;
    
    private final Indexable4 _delegate;
    
    public FieldIndexKeyHandler(YapStream stream, Indexable4 delegate_) {
        _integerHandler = new YInt(stream);
        _delegate = delegate_;
    }

    public Object comparableObject(Transaction trans, Object indexEntry) {
        return indexEntry;
    }

    public int linkLength() {
        return _delegate.linkLength() + YapConst.INT_LENGTH;
    }

    public Object readIndexEntry(YapReader a_reader) {
        Integer intPart = (Integer)_integerHandler.readIndexEntry(a_reader);
        Object objPart = _delegate.readIndexEntry(a_reader);
        return new FieldIndexKey(intPart.intValue(), objPart);
    }

    public void writeIndexEntry(YapReader writer, Object obj) {
        FieldIndexKey composite = cast(obj);
        _integerHandler.write(composite.parentID(), writer);
        _delegate.writeIndexEntry(writer, composite.value());
    }
    
    private FieldIndexKey cast(Object obj){
        return (FieldIndexKey)obj;
    }

    public YapComparable prepareComparison(Object obj) {
        FieldIndexKey composite = cast(obj);
        _delegate.prepareComparison(composite.value());
        _integerHandler.prepareComparison(composite.parentID());
        return this;
    }

    public int compareTo(Object obj) {
    	if (null == obj) {
    		throw new ArgumentNullException();
    	}
        FieldIndexKey composite = cast(obj);
        int delegateResult = _delegate.compareTo(composite.value());  
        if(delegateResult != 0 ){
            return delegateResult;
        }
        return _integerHandler.compareTo(composite.parentID());
    }

    public boolean isEqual(Object obj) {
        return compareTo(obj) == 0;
    }

    public boolean isGreater(Object obj) {
        return compareTo(obj) > 0;
    }

    public boolean isSmaller(Object obj) {
        return compareTo(obj) < 0;
    }

    public Object current() {
        return new FieldIndexKey(_integerHandler.currentInt(), _delegate.current());  
    }

}

