/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.foundation.network;

import com.db4o.ext.*;

/**
 * Fakes a socket connection for an embedded client.
 */
public class LoopbackSocket implements Socket4 {
	
	private final LoopbackSocketServer _server;

    private LoopbackSocket _affiliate;
    private BlockingByteChannel _uploadBuffer;
    private BlockingByteChannel _downloadBuffer;

    public LoopbackSocket(LoopbackSocketServer a_server, int timeout) {
    	_server = a_server;
        _uploadBuffer = new BlockingByteChannel(timeout);
        _downloadBuffer = new BlockingByteChannel(timeout);
    }

    public LoopbackSocket(LoopbackSocketServer a_server, int timeout, LoopbackSocket affiliate) {
        this(a_server, timeout);
        _affiliate = affiliate;
        affiliate._affiliate = this;
        _downloadBuffer = affiliate._uploadBuffer;
        _uploadBuffer = affiliate._downloadBuffer;
    }

    public void close() throws Db4oIOException {
        closeAffiliate();
        closeSocket();
    }

	private void closeAffiliate() throws Db4oIOException {
		if (_affiliate != null) {
            LoopbackSocket temp = _affiliate;
            _affiliate = null;
            temp.close();
        }
	}

	private void closeSocket() {
        _downloadBuffer.close();
        _uploadBuffer.close();
	}

    public void flush() {
        // do nothing
    }

    public boolean isConnected() {
        return _affiliate != null;
    }

    public int read() throws Db4oIOException {
        return _downloadBuffer.read();
    }

    public int read(byte[] a_bytes, int a_offset, int a_length) throws Db4oIOException {
        return _downloadBuffer.read(a_bytes, a_offset, a_length);
    }

    public void setSoTimeout(int a_timeout) {
        _uploadBuffer.setTimeout(a_timeout);
        _downloadBuffer.setTimeout(a_timeout);
    }

    public void write(byte[] bytes) throws Db4oIOException {
        _uploadBuffer.write(bytes);
    }
    
    public void write(byte[] bytes,int off,int len) throws Db4oIOException {
        _uploadBuffer.write(bytes, off, len);
    }

    public void write(int i) throws Db4oIOException {
        _uploadBuffer.write(i);
    }
    
    public Socket4 openParalellSocket() throws Db4oIOException {
    	return _server.openClientSocket();
    }
}
