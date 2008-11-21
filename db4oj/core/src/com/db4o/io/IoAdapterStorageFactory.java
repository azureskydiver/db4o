package com.db4o.io;

import com.db4o.ext.*;

public class IoAdapterStorageFactory implements StorageFactory {
	
	private final IoAdapter _io;

	public IoAdapterStorageFactory(IoAdapter io) {
		_io = io;
	}

	public boolean exists(String uri) {
		return _io.exists(uri);
	}

	public Storage open(String uri, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
		return new IoAdapterStorage(_io.open(uri, lockFile, initialLength, readOnly));
	}
	
	static  class IoAdapterStorage implements Storage {

		private final IoAdapter _io;

		public IoAdapterStorage(IoAdapter io) {
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

		public void write(long position, byte[] bytes, int bytesToWrite) {
			_io.seek(position);
			_io.write(bytes, bytesToWrite);
		}

	}

}
