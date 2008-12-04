/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */
package com.db4o.io;

import com.db4o.ext.*;

/**
 * @exclude
 */
@SuppressWarnings("deprecation")
public class IoAdapterStorage implements Storage {
	
	private final IoAdapter _io;

	public IoAdapterStorage(IoAdapter io) {
		_io = io;
	}

	public boolean exists(String uri) {
		return _io.exists(uri);
	}

	public Bin open(BinConfiguration config) throws Db4oIOException {
		return new IoAdapterBin(_io.open(config.uri(), config.lockFile(), config.initialLength(), config.readOnly()));
	}
	
	
	static  class IoAdapterBin implements BlockSizeAwareBin {

		private final IoAdapter _io;

		public IoAdapterBin(IoAdapter io) {
			_io = io;
	    }
		
		public void close() {
			_io.close();
		}
		
		public long length() {
			return _io.getLength();
		}
		
		public int read(long position, byte[] buffer, int bytesToRead) {
			_io.seek(position);
			return _io.read(buffer, bytesToRead);
		}
		
		public void sync() {
			_io.sync();
		}
		
		public int syncRead(long position, byte[] bytes, int bytesToRead) {
			return read(position, bytes, bytesToRead);
		}
		
		public void write(long position, byte[] bytes, int bytesToWrite) {
			_io.seek(position);
			_io.write(bytes, bytesToWrite);
		}
		
		public void blockSize(int blockSize) {
			_io.blockSize(blockSize);
		}

	}

}
