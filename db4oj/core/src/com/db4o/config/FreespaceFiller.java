/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.config;

import java.io.*;

import com.db4o.io.*;

/**
 * Callback hook for overwriting freed space in the database file.
 */
public interface FreespaceFiller {
	/**
	 * Called to overwrite freed space in the database file.
	 * 
	 * @param io Handle for the freed slot
	 */
	void fill(IoAdapterWindow io) throws IOException;
}
