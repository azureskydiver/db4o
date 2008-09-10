/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.config;

import com.db4o.*;
import com.db4o.ext.*;

/**
 * factory to open C/S server and client implementations.
 * @see Db4o#openClient(Configuration, String, int, String, String, NativeSocketFactory)
 * @see Db4o#openServer(Configuration, String, int, NativeSocketFactory) 
 */
public interface ClientServerFactory {
	
	public ObjectContainer openClient(
			Configuration config,
			String hostName, 
			int port, 
			String user, 
			String password, 
			NativeSocketFactory socketFactory)
				throws 
					Db4oIOException, 
					OldFormatException,
					InvalidPasswordException ;
	
	
	public ObjectServer openServer(
			Configuration config,
			String databaseFileName, 
			int port, 
			NativeSocketFactory socketFactory) 
				throws 
					Db4oIOException,
					IncompatibleFileFormatException, 
					OldFormatException,
					DatabaseFileLockedException, 
					DatabaseReadOnlyException;

}
