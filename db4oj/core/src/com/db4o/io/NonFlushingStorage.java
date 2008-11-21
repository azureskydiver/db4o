/**
 * 
 */
package com.db4o.io;

class NonFlushingStorage extends StorageDecorator {

	NonFlushingStorage(Storage storage) {
		super(storage);
    }
	
	@Override
	public void sync() {
	}
	
}