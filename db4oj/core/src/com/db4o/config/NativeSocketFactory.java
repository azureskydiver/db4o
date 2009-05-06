/* Copyright (C) 2007  Versant Inc.  http://www.db4o.com */

package com.db4o.config;

import java.io.*;
import java.net.*;

import com.db4o.foundation.*;

/**
 * Create platform native server and client sockets.
 */
public interface NativeSocketFactory extends DeepClone {
	Socket createSocket(String hostName, int port) throws IOException;
	ServerSocket createServerSocket(int port) throws IOException;
}
