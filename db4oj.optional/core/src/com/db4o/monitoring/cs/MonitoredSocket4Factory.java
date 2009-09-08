/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.monitoring.cs;

import java.io.IOException;
import com.db4o.foundation.network.*;

@decaf.Ignore
public class MonitoredSocket4Factory implements Socket4Factory {
	public MonitoredSocket4Factory(Socket4Factory socketFactory) {
		_socketFactory = socketFactory;
	}

	public ServerSocket4 createServerSocket(final int port) throws IOException {
		return new MonitoredServerSocket4(_socketFactory.createServerSocket(port));
	}
	
	public Socket4 createSocket(String hostName, int port) throws IOException {
		return new ClientMonitoredSocket4(_socketFactory.createSocket(hostName, port));
	}

	private Socket4Factory _socketFactory;
}
