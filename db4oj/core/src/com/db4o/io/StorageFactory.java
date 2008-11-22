package com.db4o.io;

import com.db4o.ext.*;

public interface StorageFactory {
	
	public interface Bin {

		long length();

		int read(long position, byte[] buffer, int bytesToRead);
		
		void write(long position, byte[] bytes, int bytesToWrite);
		
		void sync();

		void close();

	}

	Bin open(String uri, boolean lockFile,
			long initialLength, boolean readOnly) throws Db4oIOException;

	boolean exists(String uri);
}
