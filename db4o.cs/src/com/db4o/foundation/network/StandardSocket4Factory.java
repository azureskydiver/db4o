package com.db4o.foundation.network;

import java.io.*;

public class StandardSocket4Factory implements Socket4Factory {

	public ServerSocket4 createServerSocket(int port) throws IOException {
		return new NetworkServerSocket(port);
	}

	public Socket4 createSocket(String hostName, int port) throws IOException {
		return new NetworkSocket(hostName, port);
	}

}
