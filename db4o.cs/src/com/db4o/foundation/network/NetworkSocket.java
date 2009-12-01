/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.foundation.network;

import java.io.*;
import java.net.*;

public class NetworkSocket extends NetworkSocketBase {

	private Socket _socket;
	
    public NetworkSocket(String hostName, int port) throws IOException {
    	super(hostName);
    	_socket = new Socket(hostName, port);
    }

	public NetworkSocket(Socket socket) throws IOException {
    	_socket = socket;
    }

	@Override
	protected Socket socket() {
		return _socket;
	}

	@Override
	protected Socket4 createParallelSocket(String hostName, int port) throws IOException {
		return new NetworkSocket(hostName, port);
	}

}
