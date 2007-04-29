/* Copyright (C) 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.*;


/**
 * this Exception is thrown during any of the db4o open calls
 * if the database file is locked by another process.
 * @see com.db4o.Db4o#openFile
 */
public class DatabaseFileLockedException extends Db4oException {
	
	public DatabaseFileLockedException(String databaseDescription) {
		this(databaseDescription,null);
	}

	public DatabaseFileLockedException(String databaseDescription, Throwable cause) {
		super(databaseDescription,cause);
	}
	
}
