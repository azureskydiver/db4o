package com.db4o.io;

import com.db4o.ext.*;

public class NonFlushingStorageFactory implements StorageFactory {

	private final StorageFactory _factory;

	public NonFlushingStorageFactory(StorageFactory factory) {
		_factory = factory;
    }

	public boolean exists(String uri) {
		return _factory.exists(uri);
	}

	public Storage open(String uri, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
		final Storage storage = _factory.open(uri, lockFile, initialLength, readOnly);
		return new NonFlushingStorage(storage);
	}

}
