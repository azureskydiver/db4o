package com.db4o.replication.db4o;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.YapStream;

import java.io.IOException;

public class Db4oClientServerReplicationProvider extends Db4oReplicationProvider {
	private String serverHostName;
	private int serverPort;
	private String userName;
	private String password;

	public Db4oClientServerReplicationProvider(ObjectContainer objectContainer) {
		super(objectContainer);
		throw new UnsupportedOperationException();
	}

	public Db4oClientServerReplicationProvider(ObjectContainer objectContainer, String name, String serverHostName, int serverPort, String userName, String password) {
		super(objectContainer, name);

		this.serverHostName = serverHostName;
		this.serverPort = serverPort;
		this.userName = userName;
		this.password = password;
	}

	private void reOpen() {
//		try {
//			_stream = (YapStream) Db4o.openClient(serverHostName, serverPort, userName, password);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//
//		_reflector = _stream.reflector();
//		_signatureMap = new Db4oSignatureMap(_stream);

	}

//	public void commitReplicationTransaction(long raisedDatabaseVersion) {
//		super.commitReplicationTransaction(raisedDatabaseVersion);
//		_stream.close();
//		reOpen();
//	}
}
