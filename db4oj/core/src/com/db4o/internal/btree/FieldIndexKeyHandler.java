/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.btree;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.handlers.*;


/**
 * @exclude
 */
public class FieldIndexKeyHandler implements Indexable4{
	
    private final Indexable4 _valueHandler;
    
    private final IntHandler _parentIdHandler;
    
    public FieldIndexKeyHandler(ObjectContainerBase stream, Indexable4 delegate_) {
        _parentIdHandler = new IDHandler(stream);
        _valueHandler = delegate_;
    }

    public int linkLength() {
        return _valueHandler.linkLength() + Const4.INT_LENGTH;
    }

    public Object readIndexEntry(Buffer a_reader) {
        // TODO: could read int directly here with a_reader.readInt()
        int parentID = readParentID(a_reader);
        Object objPart = _valueHandler.readIndexEntry(a_reader);
        if (parentID < 0){
            objPart = null;
            parentID = - parentID;
        }
        return new FieldIndexKey(parentID, objPart);
    }

	private int readParentID(Buffer a_reader) {
		return ((Integer)_parentIdHandler.readIndexEntry(a_reader)).intValue();
	}

    public void writeIndexEntry(Buffer writer, Object obj) {
        FieldIndexKey composite = (FieldIndexKey)obj;
        int parentID = composite.parentID();
        Object value = composite.value();
        if (value == null){
            parentID = - parentID;
        }
        _parentIdHandler.write(parentID, writer);
        _valueHandler.writeIndexEntry(writer, composite.value());
    }
    
    public Indexable4 valueHandler() {
    	return _valueHandler;
    }
    
    public Comparable4 prepareComparison(Object obj) {
        FieldIndexKey composite = (FieldIndexKey)obj;
        _valueHandler.prepareComparison(composite.value());
        _parentIdHandler.prepareComparison(composite.parentID());
        return this;
    }

    public int compareTo(Object obj) {
    	if (null == obj) {
    		throw new ArgumentNullException();
    	}
        FieldIndexKey composite = (FieldIndexKey)obj;
        int delegateResult = _valueHandler.compareTo(composite.value());  
        if(delegateResult != 0 ){
            return delegateResult;
        }
        return _parentIdHandler.compareTo(composite.parentID());
    }

    public boolean isEqual(Object obj) {
    	throw new NotImplementedException();
    }

    public boolean isGreater(Object obj) {
    	throw new NotImplementedException();
    }

    public boolean isSmaller(Object obj) {
    	throw new NotImplementedException();
    }

    public Object current() {
        return new FieldIndexKey(_parentIdHandler.currentInt(), _valueHandler.current());  
    }

	public void defragIndexEntry(ReaderPair readers) {
		_parentIdHandler.defragIndexEntry(readers);
        _valueHandler.defragIndexEntry(readers);
	}
}

