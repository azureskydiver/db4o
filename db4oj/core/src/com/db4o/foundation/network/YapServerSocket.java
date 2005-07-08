/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.foundation.network;

import java.io.*;
import java.net.*;

/**
 * @exclude
 */
public class YapServerSocket {

    private ServerSocket _serverSocket;

    public YapServerSocket(int port) throws IOException {
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

    public YapSocket accept() throws IOException {
        Socket sock = _serverSocket.accept();
        // TODO: check connection permissions here
        return new YapSocketReal(sock);
    }
	
	public void close() throws IOException {
		_serverSocket.close();
	}

}
