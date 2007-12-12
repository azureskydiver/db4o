/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;


/**
 * @exclude
 */
public class DefragmentContextImpl extends BufferContext implements DefragmentContext{
	
	private final int _handlerVersion;
	
	public DefragmentContextImpl(int handlerVersion, Buffer readers) {
		super(((BufferPair)readers).services().systemTrans(), readers);
		_handlerVersion = handlerVersion;
	}
	
	private BufferPair buffers() {
		return (BufferPair)_buffer;
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

	public Buffer sourceBuffer() {
		return buffers().source();
	}

	public Buffer targetBuffer() {
		return buffers().target();
	}

	public int copyIDReturnOriginalID() {
		return buffers().copyIDReturnOriginalID();
	}

	public int handlerVersion() {
		return _handlerVersion;
	}

	public ClassMetadata classMetadataForId(int id) {
		return container().classMetadataForId(id);
	}

	public int mappedID(int origID) {
		return buffers().mapping().mappedID(origID);
	}

}
