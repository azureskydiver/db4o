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

}
