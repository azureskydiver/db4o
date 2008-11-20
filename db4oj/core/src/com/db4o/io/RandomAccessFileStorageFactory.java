package com.db4o.io;

import com.db4o.ext.*;

public class RandomAccessFileStorageFactory implements StorageFactory {

	// TODO: replace readOnly by ReadOnlyStorage decorator
	public Storage open(String uri, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
		return new RandomAccessFileStorage(uri, lockFile, initialLength, readOnly);
    }

}
