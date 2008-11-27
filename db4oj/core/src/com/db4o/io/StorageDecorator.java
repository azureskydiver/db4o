/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */
package com.db4o.io;

import com.db4o.ext.*;

/**
 * @exclude
 */
public class StorageDecorator implements Storage {

	protected final Storage _storage;

	public StorageDecorator(Storage storage) {
		_storage = storage;
	}

	public boolean exists(String uri) {
    	return _storage.exists(uri);
    }
	
	public Bin open(String uri, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
		return decorate( _storage.open(uri, lockFile, initialLength, readOnly));
	}

	protected Bin decorate(Bin bin) {
		return bin;
    }
}