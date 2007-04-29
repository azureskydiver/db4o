/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.*;


/**
 * this Exception is thrown during any of the db4o open calls
 * if the database file is locked by another process.
 * @see com.db4o.Db4o#openFile
 */
public class DatabaseFileLockedException extends Db4oException {
	
	private String _databaseDescription;
	
	public DatabaseFileLockedException(String databaseDescription) {
		this(databaseDescription,null);
	}

	public DatabaseFileLockedException(String databaseDescription, Throwable cause) {
		super(message(databaseDescription),cause);
		_databaseDescription=databaseDescription;
	}

	public String databaseDescription() {
		return _databaseDescription;
	}
	
	private static String message(String databaseDescription) {
		return "Database locked: '"+databaseDescription+"'";
	}
}
