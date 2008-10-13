/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.foundation.network;

import java.io.*;
import java.net.*;

import com.db4o.config.*;

public class ServerSocket4 {

    private ServerSocket _serverSocket;
    private NativeSocketFactory _factory;

    public ServerSocket4(NativeSocketFactory factory, int port) throws IOException {
        _factory = factory;
        _serverSocket = _factory.createServerSocket(port);
    }

    public void setSoTimeout(int timeout) {
        try {
            _serverSocket.setSoTimeout(timeout);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public int getLocalPort() {
        return _serverSocket.getLocalPort();
    }

    public Socket4 accept() throws IOException {
        Socket sock = _serverSocket.accept();
        // TODO: check connection permissions here
        return new NetworkSocket(_factory, sock);
    }
	
	public void close() throws IOException {
		_serverSocket.close();
	}

}
