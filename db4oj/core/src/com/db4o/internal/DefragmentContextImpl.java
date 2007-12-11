/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.internal.mapping.*;
import com.db4o.internal.marshall.*;

/**
 * @exclude
 */
public class DefragmentContextImpl extends BufferContext implements DefragmentContext{
	
	private MarshallerFamily _mf;
	
	boolean _redirect;
	
	public DefragmentContextImpl(DefragmentContext context, boolean redirect) {
		this(((DefragmentContextImpl)context)._mf, ((DefragmentContextImpl)context)._buffer, redirect);
	}
	
	public DefragmentContextImpl(MarshallerFamily mf, Buffer readers, boolean redirect) {
		super(((BufferPair)readers).services().systemTrans(), readers);
		_mf= mf;
		_redirect = redirect;
	}
	
	public MarshallerFamily marshallerFamily() {
		return _mf;
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
		return 0;
	}	

}
