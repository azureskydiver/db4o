/* Copyright (C) 2008  db4objects Inc.   http://www.db4o.com */
package com.db4o.io;

import com.db4o.ext.*;

public class MemoryBin implements Bin {
	
	private byte[] _bytes;
	private int _length;
	private GrowthStrategy _growthStrategy;

	public MemoryBin(byte[] bytes, GrowthStrategy growthStrategy) {
		_bytes = bytes;
		_length = bytes.length;
		_growthStrategy = growthStrategy;
    }
	
	public long length() {
		return _length;
	}
	
	public long bufferSize() {
		return _bytes.length;
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
			long newSize = _growthStrategy.newSize(_bytes.length);
			if (pos + length > newSize) {
				newSize = pos + length;
			}
			byte[] temp = new byte[(int)newSize];
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