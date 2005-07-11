package com.db4o.foundation.network;

import java.io.*;
import java.net.*;

public class YapClientSocket extends YapSocketReal {
    protected String _hostName;
    protected int _port;

    public YapClientSocket(String hostName, int port) throws IOException {
        super(new Socket(hostName, port));
        _hostName = hostName;
		_port = port;
    }

	public YapSocket openParalellSocket() throws IOException {
		return new YapClientSocket(_hostName, _port);
	}

}
