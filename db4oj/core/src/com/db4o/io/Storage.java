/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */
package com.db4o.io;

import com.db4o.config.*;
import com.db4o.ext.*;

/**
 * Base interface for Storage adapters that open a {@link Bin}
 * to store db4o database data to.
 * @see FileConfiguration#storage(Storage) 
 */
public interface Storage {
	
	/**
	 * opens a {@link Bin} to store db4o database data. 
	 */
	Bin open(String uri, boolean lockFile,
			long initialLength, boolean readOnly) throws Db4oIOException;

	/**
	 * returns true if a Bin (file or memory) exists with the passed name. 
	 */
	boolean exists(String uri);
}
