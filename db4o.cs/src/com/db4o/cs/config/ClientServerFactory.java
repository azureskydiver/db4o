/* Copyright (C) 2008  Versant Inc.  http://www.db4o.com */

package com.db4o.cs.config;

import com.db4o.*;
import com.db4o.ext.*;

/**
 * factory to open C/S server and client implementations.
 * @see Db4o#openClient(ClientConfiguration, String, int, String, String)
 * @see Db4o#openServer(ServerConfiguration, String, int) 
 */
public interface ClientServerFactory {
	
	public ObjectContainer openClient(
			ClientConfiguration config,
			String hostName, 
			int port, 
			String user, 
			String password)
				throws 
					Db4oIOException, 
					OldFormatException,
					InvalidPasswordException ;
	
	
	public ObjectServer openServer(
			ServerConfiguration config,
			String databaseFileName, 
			int port) 
				throws 
					Db4oIOException,
					IncompatibleFileFormatException, 
					OldFormatException,
					DatabaseFileLockedException, 
					DatabaseReadOnlyException;

}
