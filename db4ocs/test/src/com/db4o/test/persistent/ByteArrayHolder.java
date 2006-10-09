/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.persistent;


public class ByteArrayHolder implements IByteArrayHolder {

	byte[] _bytes;

	public ByteArrayHolder(byte[] bytes) {
		this._bytes = bytes;
	}

	public byte[] getBytes() {
		return _bytes;
	}
}
