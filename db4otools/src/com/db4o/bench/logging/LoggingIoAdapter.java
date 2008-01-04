/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.bench.logging;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.io.*;

public class LoggingIoAdapter extends VanillaIoAdapter {
	
	public final static int LOG_READ 	= 1;
	public final static int LOG_WRITE 	= 2;
	public final static int LOG_SYNC 	= 4;
	public final static int LOG_SEEK    = 8;
	
	public final static int LOG_ALL = LOG_READ + LOG_WRITE + LOG_SYNC + LOG_SEEK;
	
	private final String _fileName;
	private final PrintStream _out;
	
	private int _config;
	
    public LoggingIoAdapter(IoAdapter delegateAdapter, String fileName, int config)  {
        super(delegateAdapter);
        _fileName = fileName;
        try {
			_out = new PrintStream(new FileOutputStream(fileName));
		} catch (FileNotFoundException e) {
			throw new Db4oIOException(e);
		}
        _config = config;
    }
    
    public LoggingIoAdapter(IoAdapter delegateAdapter, String fileName)  {
    	this(delegateAdapter, fileName, LOG_ALL);
    }

    private LoggingIoAdapter(IoAdapter delegateAdapter, String path, boolean lockFile, long initialLength, String fileName, int config) throws Db4oIOException{
        this(delegateAdapter.open(path, lockFile, initialLength, false), fileName, config);
    }

	public IoAdapter open(String path, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
		return new LoggingIoAdapter(_delegate, path, lockFile, initialLength, _fileName, _config);
	}
	
	public void close() throws Db4oIOException {
		super.close();
		_out.flush();
		_out.close();
	}

    public int read(byte[] bytes, int length) throws Db4oIOException {
    	if(config(LOG_READ)) {
    		println(LogConstants.READ_ENTRY + length);
    	}
        return _delegate.read(bytes, length);
    }

    public void seek(long pos) throws Db4oIOException {
    	if(config(LOG_SEEK)) {
    		println(LogConstants.SEEK_ENTRY + pos);
    	}
        _delegate.seek(pos);
    }

    public void sync() throws Db4oIOException {
    	if(config(LOG_SYNC)) {
    		println(LogConstants.SYNC_ENTRY);
    	}
        _delegate.sync();
    }

    public void write(byte[] buffer, int length) throws Db4oIOException {
    	if(config(LOG_WRITE)) {
    		println(LogConstants.WRITE_ENTRY + length);
    	}
        _delegate.write(buffer, length);
    }
    
    private void println(String s){
    	_out.println(s);
    }

    private boolean config(int mask) {
    	return (_config&mask)!=0;
    }
    
}
