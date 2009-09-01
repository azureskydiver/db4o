package com.db4o.foundation.network;

import java.io.*;

public interface Socket4Factory {

	Socket4 createSocket(String hostName, int port) throws IOException;
	ServerSocket4 createServerSocket(int port) throws IOException;
}
