/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */
package com.db4o.io;

import com.db4o.ext.*;

/**
 * Wrapper baseclass for all classes that wrap Storage.
 * Each class that adds functionality to a Storage must
 * extend this class.
 * @see BinDecorator 
 */
public class StorageDecorator implements Storage {

	protected final Storage _storage;

	public StorageDecorator(Storage storage) {
		_storage = storage;
	}

	public boolean exists(String uri) {
    	return _storage.exists(uri);
    }
	
	public Bin open(BinConfiguration config) throws Db4oIOException {
		return decorate( _storage.open(config));
	}

	protected Bin decorate(Bin bin) {
		return bin;
    }
}