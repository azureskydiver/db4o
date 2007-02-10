package com.db4o.drs.test;

public class ByteArrayHolder implements IByteArrayHolder {
	
	private byte[] _bytes;
	
	public ByteArrayHolder() {
	}
	
	public ByteArrayHolder(byte[] bytes) {
		this._bytes = bytes;
	}
	
	public byte[] getBytes() {
		return _bytes;
	}

	public void setBytes(byte[] bytes) {
		_bytes = bytes;
	}
}
