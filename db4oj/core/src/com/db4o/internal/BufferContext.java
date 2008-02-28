/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.marshall.*;

/**
 * @exclude
 */
public abstract class BufferContext implements ReadBuffer{
	
	protected ReadWriteBuffer _buffer;
	protected final Transaction _transaction;
	
	public BufferContext(Transaction transaction, ReadWriteBuffer buffer) {
		_transaction = transaction;
        _buffer = buffer;
	}

	public ReadWriteBuffer buffer(ReadWriteBuffer buffer) {
	    ReadWriteBuffer temp = _buffer;
	    _buffer = buffer;
	    return temp;
	}

	public ReadWriteBuffer buffer() {
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
	    return container().handlers().correctHandlerVersion(handler, handlerVersion());
	}

	public abstract int handlerVersion();
	
	public boolean isLegacyHandlerVersion() {
		return handlerVersion() == 0;
	}
	
    public BitMap4 readBitMap(int bitCount){
        return _buffer.readBitMap(bitCount);
    }
    
    public void seekCurrentInt() {
        seek(readInt());
    }


}
