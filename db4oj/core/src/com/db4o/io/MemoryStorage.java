/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */
package com.db4o.io;

import java.util.*;

import com.db4o.ext.*;

/**
 * {@link Storage} implementation that produces {@link Bin} instances
 * that operate in memory.
 * Use this {@link Storage} to work with db4o as an in-memory database. 
 */
public class MemoryStorage implements Storage {

	private final Map<String, Bin> _storages = new HashMap<String, Bin>();

	/**
	 * returns true if a MemoryBin with the given URI name already exists
	 * in this Storage.
	 */
	public boolean exists(String uri) {
		return _storages.containsKey(uri);
	}

	/**
	 * opens a MemoryBin for the given URI (name can be freely chosen).
	 */
	public Bin open(BinConfiguration config) throws Db4oIOException {
		final Bin storage = produceStorage(config);
		return config.readOnly() ? new ReadOnlyBin(storage) : storage;
	}

	private Bin produceStorage(BinConfiguration config) {
	    final Bin storage = _storages.get(config.uri());
		if (null != storage) {
			return storage;
		}
		final MemoryBin newStorage = new MemoryBin(new byte[(int)config.initialLength()]);
		_storages.put(config.uri(), newStorage);
		return newStorage;
    }
	
	private static class MemoryBin implements Bin {
		
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

}
