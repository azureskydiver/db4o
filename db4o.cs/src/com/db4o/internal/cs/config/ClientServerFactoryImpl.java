/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.config;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.network.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.*;

/**
 * @exclude
 */
public class ClientServerFactoryImpl implements ClientServerFactory{

	public ObjectContainer openClient(Configuration config, String hostName,
			int port, String user, String password,
			NativeSocketFactory socketFactory) throws Db4oIOException,
			OldFormatException, InvalidPasswordException {
		if (user == null || password == null) {
			throw new InvalidPasswordException();
		}
		NetworkSocket networkSocket = new NetworkSocket(socketFactory, hostName, port);
		return new ClientObjectContainer(config, networkSocket, user, password, true);
	}

	public ObjectServer openServer(Configuration config,
			String databaseFileName, int port, NativeSocketFactory socketFactory)
			throws Db4oIOException, IncompatibleFileFormatException,
			OldFormatException, DatabaseFileLockedException,
			DatabaseReadOnlyException {
		LocalObjectContainer container = (LocalObjectContainer)Db4oEmbedded.openFile(config,databaseFileName);
        if(container == null){
            return null;
        }
        synchronized(container.lock()){
            return new ObjectServerImpl(container, port, socketFactory);
        }
	}

}
