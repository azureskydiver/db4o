/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.foundation.network;

import java.io.*;
import java.net.*;

public class ServerSocket4 {

    private ServerSocket _serverSocket;

    public ServerSocket4(int port) throws IOException {
        _serverSocket = new ServerSocket(port);
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
        return new NetworkSocket(sock);
    }
	
	public void close() throws IOException {
		_serverSocket.close();
	}

}
