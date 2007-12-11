/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;

/**
 * @exclude
 */
public abstract class BufferContext implements ReadBuffer{
	
	protected Buffer _buffer;
	protected final Transaction _transaction;
	
	public BufferContext(Transaction transaction, Buffer buffer) {
		_transaction = transaction;
        _buffer = buffer;
	}

	public Buffer buffer(Buffer buffer) {
	    Buffer temp = _buffer;
	    _buffer = buffer;
	    return temp;
	}

	public Buffer buffer() {
	    return _buffer;
	}

	public byte readByte() {
	    return _buffer.readByte();
	}

	public void readBytes(byte[] bytes) {
	    _buffer.readBytes(bytes);
	}

	public int readInt() {
	    return _buffer.readInt();
	}

	public long readLong() {
	    return _buffer.readLong();
	}

	public int offset() {
	    return _buffer.offset();
	}

	public void seek(int offset) {
	    _buffer.seek(offset);
	}

	public ObjectContainerBase container() {
	    return _transaction.container();
	}

	public ObjectContainer objectContainer() {
	    return (ObjectContainer) container();
	}

	public Transaction transaction() {
	    return _transaction;
	}

	public TypeHandler4 correctHandlerVersion(TypeHandler4 handler) {
	    if(! oldHandlerVersion()){
	        return handler;
	    }
	    return container().handlers().correctHandlerVersion(handler, handlerVersion());
	}

	public boolean oldHandlerVersion() {
	    return handlerVersion() != MarshallingContext.HANDLER_VERSION;
	}

	public abstract int handlerVersion();
	
	public boolean isLegacyHandlerVersion() {
		return handlerVersion() == 0;
	}



}
