/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.persistent;

import java.io.Serializable;

public class SerializableByteArrayHolder implements Serializable, IByteArrayHolder {

	private static final long serialVersionUID = 1L;

	public byte[] _bytes;

	public SerializableByteArrayHolder(byte[] bytes) {
		this._bytes = bytes;
	}

	public byte[] getBytes() {
		return _bytes;
	}
}

