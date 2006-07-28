/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.acid;

import java.io.*;

import com.db4o.io.*;

public class LoggingIoAdapter extends VanillaIoAdapter {
	private final static int READ=1;
	private final static int WRITE=2;
	private final static int SYNC=4;
	
	private PrintStream _out;
	private int _config;
	private long _curpos;
	
    public LoggingIoAdapter(IoAdapter delegateAdapter,PrintStream out) {
    	this(delegateAdapter,out,WRITE);
    }

    public LoggingIoAdapter(IoAdapter delegateAdapter,PrintStream out,int config) {
        super(delegateAdapter);
        _out=out;
        _config=config;
    }

    private LoggingIoAdapter(IoAdapter delegateAdapter, String path, boolean lockFile, long initialLength, PrintStream out,int config) throws IOException {
        super(delegateAdapter.open(path, lockFile, initialLength));
        _out=out;
        _config=config;
        _curpos=0;
    }

	public IoAdapter open(String path, boolean lockFile, long initialLength) throws IOException {
		return new LoggingIoAdapter(_delegate,path,lockFile,initialLength,_out,_config);
	}

    public int read(byte[] bytes, int length) throws IOException {
    	if(config(READ)) {
    		_out.println("READ "+_curpos+","+length);
    	}
        return _delegate.read(bytes, length);
    }

    public void seek(long pos) throws IOException {
    	_curpos=pos;
        _delegate.seek(pos);
    }

    public void sync() throws IOException {
    	if(config(SYNC)) {
    		_out.println("SYNC");
    	}
        _delegate.sync();
    }

    public void write(byte[] buffer, int length) throws IOException {
    	if(config(WRITE)) {
    		_out.println("WRITE "+_curpos+","+length);
    	}
        _delegate.write(buffer, length);
    }

    private boolean config(int mask) {
    	return (_config&mask)!=0;
    }
}
