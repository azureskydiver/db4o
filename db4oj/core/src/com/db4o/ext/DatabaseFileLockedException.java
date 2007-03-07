/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.ext;

import com.db4o.foundation.ChainedRuntimeException;

/**
 * this Exception is thrown during any of the db4o open calls
 * if the database file is locked by another process.
 * @see com.db4o.Db4o#openFile
 */
public class DatabaseFileLockedException extends ChainedRuntimeException {
	public DatabaseFileLockedException() {
	}
	
	public DatabaseFileLockedException(Exception cause) {
		super(cause.getMessage(), cause);
	}
}
