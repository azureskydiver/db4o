package com.db4o.drs.test.db4o;

import java.io.Serializable;

import com.db4o.drs.test.IByteArrayHolder;

public class SerializableByteArrayHolder implements Serializable, IByteArrayHolder{
	private static final long serialVersionUID = 1L;
	
	private byte[] _bytes;
	
	public SerializableByteArrayHolder() {
	}
	
	public SerializableByteArrayHolder(byte[] bytes) {
		this._bytes = bytes;
	}
	
	public byte[] getBytes() {
		return _bytes;
	}

	public void setBytes(byte[] bytes) {
		_bytes = bytes;
	}	
}
