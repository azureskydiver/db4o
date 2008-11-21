package com.db4o.io;

import com.db4o.ext.*;

public class ReadOnlyStorage extends StorageDecorator {

	public ReadOnlyStorage(Storage storage) {
	    super(storage);
    }
	
	@Override
	public void write(long position, byte[] bytes, int bytesToWrite) {
	    throw new Db4oIOException();
	}

}
