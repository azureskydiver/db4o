package com.db4o.io;

import com.db4o.ext.*;

public interface StorageFactory {

	Storage open(String uri, boolean lockFile,
			long initialLength, boolean readOnly) throws Db4oIOException;

	boolean exists(String uri);
}
