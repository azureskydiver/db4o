package com.db4o.io;

import com.db4o.ext.*;

public class StorageFactoryDecorator implements Storage {

	protected final Storage _factory;

	public StorageFactoryDecorator(Storage factory) {
		_factory = factory;
	}

	public boolean exists(String uri) {
    	return _factory.exists(uri);
    }
	
	public Bin open(String uri, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
		return decorate( _factory.open(uri, lockFile, initialLength, readOnly));
	}

	protected Bin decorate(Bin storage) {
		return storage;
    }

}