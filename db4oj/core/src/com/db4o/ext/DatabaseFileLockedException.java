/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.ext;

/**
 * this Exception is thrown during any of the db4o open calls
 * if the database file is locked by another process.
 * @see <a href="../Db4o.html#openFile(java.lang.String)">
 * <code>Db4o.openFile()</code></a>.
 */
public class DatabaseFileLockedException extends RuntimeException{
}
