/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.bench;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.io.*;

public class LoggingIoAdapter extends VanillaIoAdapter {
	
	public final static int LOG_READ 	= 1;
	public final static int LOG_WRITE 	= 2;
	public final static int LOG_SYNC 	= 4;
	
	private PrintStream _out;
	private int _config;
	private long _curpos;
	
    public LoggingIoAdapter(IoAdapter delegateAdapter,PrintStream out) {
    	this(delegateAdapter,out,LOG_WRITE);
    }

    public LoggingIoAdapter(IoAdapter delegateAdapter,PrintStream out,int config) {
        super(delegateAdapter);
        _out=out;
        _config=config;
    }

    private LoggingIoAdapter(IoAdapter delegateAdapter, String path, boolean lockFile, long initialLength, PrintStream out,int config) throws Db4oIOException {
        super(delegateAdapter.open(path, lockFile, initialLength, false));
        _out=out;
        _config=config;
        _curpos=0;
    }

	public IoAdapter open(String path, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
		return new LoggingIoAdapter(_delegate,path,lockFile,initialLength,_out,_config);
	}
	
	public void close() throws Db4oIOException {
		super.close();
		_out.flush();
		_out.close();
	}

    public int read(byte[] bytes, int length) throws Db4oIOException {
    	if(config(LOG_READ)) {
    		_out.println(LogConstants.READ_ENTRY +_curpos+ LogConstants.SEPARATOR +length);
    	}
        return _delegate.read(bytes, length);
    }

    public void seek(long pos) throws Db4oIOException {
    	_curpos=pos;
        _delegate.seek(pos);
    }

    public void sync() throws Db4oIOException {
    	if(config(LOG_SYNC)) {
    		_out.println(LogConstants.SYNC_ENTRY);
    	}
        _delegate.sync();
    }

    public void write(byte[] buffer, int length) throws Db4oIOException {
    	if(config(LOG_WRITE)) {
    		_out.println(LogConstants.WRITE_ENTRY + _curpos + LogConstants.SEPARATOR + length);
    	}
        _delegate.write(buffer, length);
    }

    private boolean config(int mask) {
    	return (_config&mask)!=0;
    }
}
