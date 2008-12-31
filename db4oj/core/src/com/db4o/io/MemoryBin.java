/* Copyright (C) 2008  db4objects Inc.   http://www.db4o.com */
package com.db4o.io;

import com.db4o.ext.*;

public class MemoryBin implements Bin {
	
	private static final int GROW_BY = 10000;
	private byte[] _bytes;
	private int _length;

	public MemoryBin(byte[] bytes) {
		_bytes = bytes;
		_length = bytes.length;
    }
	
	public long length() {
		return _length;
	}
	
	public int read(long pos, byte[] bytes, int length) throws Db4oIOException {
		final long avail = _length - pos;
		if (avail <= 0) {
			return - 1;
		}
		final int read = Math.min((int)avail, length);
		System.arraycopy(_bytes, (int)pos, bytes, 0, read);
		return read;
	}

	public void sync() throws Db4oIOException {
	}
	
	public int syncRead(long position, byte[] bytes, int bytesToRead) {
		return read(position, bytes, bytesToRead);
	}
	
	public void close() {
	}

	/**
	 * Returns a copy of the raw data contained in this bin for external processing.
	 */
	public byte[] data() {
		byte[] data = new byte[_length];
		System.arraycopy(_bytes, 0, data, 0, _length);
		return data;
	}

	/**
	 * for internal processing only.
	 */
	public void write(long pos, byte[] buffer, int length) throws Db4oIOException {
		if (pos + length > _bytes.length) {
			long growBy = GROW_BY;
			if (pos + length > growBy) {
				growBy = pos + length;
			}
			byte[] temp = new byte[(int)(_bytes.length + growBy)];
			System.arraycopy(_bytes, 0, temp, 0, _length);
			_bytes = temp;
		}
		System.arraycopy(buffer, 0, _bytes, (int)pos, length);
		pos += length;
		if (pos > _length) {
			_length = (int)pos;
		}
	}
	
}