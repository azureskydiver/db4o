package com.db4o.io;

public class StorageDecorator implements StorageFactory.Bin {

	protected final StorageFactory.Bin _storage;

	public StorageDecorator(StorageFactory.Bin storage) {
		_storage = storage;
	}

	public void close() {
    	_storage.close();
    }

	public long length() {
		return _storage.length();
    }

	public int read(long position, byte[] buffer, int bytesToRead) {
    	return _storage.read(position, buffer, bytesToRead);
    }

	public void sync() {
		_storage.sync();
    }

	public void write(long position, byte[] bytes, int bytesToWrite) {
    	_storage.write(position, bytes, bytesToWrite);
    }

}