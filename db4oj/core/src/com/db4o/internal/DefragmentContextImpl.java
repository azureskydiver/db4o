/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.internal.mapping.*;

/**
 * @exclude
 */
public class DefragmentContextImpl extends BufferContext implements DefragmentContext{
	
	boolean _redirect;
	
	private final int _handlerVersion;

	
	public DefragmentContextImpl(DefragmentContext context, boolean redirect) {
		this(((DefragmentContextImpl)context).handlerVersion(), ((DefragmentContextImpl)context)._buffer, redirect);
	}
	
	public DefragmentContextImpl(int handlerVersion, Buffer readers, boolean redirect) {
		super(((BufferPair)readers).services().systemTrans(), readers);
		_handlerVersion = handlerVersion;
		_redirect = redirect;
	}
	
	private BufferPair buffers() {
		return (BufferPair)_buffer;
	}
	
	public boolean redirect() {
		return _redirect;
	}

	public void copyID() {
		buffers().copyID();
	}

	public void copyUnindexedID() {
		buffers().copyUnindexedID();
	}

	public void incrementOffset(int length) {
		buffers().incrementOffset(length);
	}

	public void readBegin(byte identifier) {
		buffers().readBegin(identifier);
	}

	public void readEnd() {
		buffers().readEnd();
	}

	public IDMapping mapping() {
		return buffers().mapping();
	}

	public Buffer sourceBuffer() {
		return buffers().source();
	}

	public Buffer targetBuffer() {
		return buffers().target();
	}

	public MappedIDPair copyIDAndRetrieveMapping() {
		return buffers().copyIDAndRetrieveMapping();
	}

	public DefragmentServices services() {
		return buffers().services();
	}

	public int handlerVersion() {
		return _handlerVersion;
	}

}
