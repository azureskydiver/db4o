/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.marshall.*;

public abstract class MockMarshallingContext {

    private final ObjectContainer _objectContainer;
    
    final Buffer _header;
    
    final Buffer _payLoad;
    
    protected Buffer _current;
    
    public MockMarshallingContext(ObjectContainer objectContainer){
        _objectContainer = objectContainer;
        _header = new Buffer(1000);
        _payLoad = new Buffer(1000);
        _current = _header;
    }

    public WriteBuffer newBuffer(int length) {
        return new Buffer(length);
    }

    public ObjectContainer objectContainer() {
        return _objectContainer;
    }

	public void useVariableLength() {
	    _current = _payLoad;
	}

	public byte readByte() {
		return _current.readByte();
	}
	
    public void readBytes(byte[] bytes) {
        _current.readBytes(bytes);
    }

	public int readInt() {
		return _current.readInt();
	}
	
	public long readLong(){
	    return _current.readLong();
	}

	public void writeByte(byte b) {
	    _current.writeByte(b);
	}

	public void writeInt(int i) {
	    _current.writeInt(i);
	}
	
    public void writeLong(long l) {
        _current.writeLong(l);
    }

    public void writeBytes(byte[] bytes) {
        _current.writeBytes(bytes);
    }
	
    public Object readObject() {
        int id = readInt();
        Object obj = container().getByID(transaction(), id);
        objectContainer().activate(obj, Integer.MAX_VALUE);
        return obj;
    }
 
    public void writeObject(Object obj) {
        int id = container().setInternal(transaction(), obj, false);
        writeInt(id);
    }
    
    public Transaction transaction(){
        return container().transaction();
    }

    protected ObjectContainerBase container() {
        return ((InternalObjectContainer) _objectContainer).container();
    }
    
	public int offset() {
		return _current.offset();
	}

	public void seek(int offset) {
		_current.seek(offset);
	}

}