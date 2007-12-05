/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench;

import java.io.*;

import org.apache.tools.ant.util.*;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.io.*;


public class BenchmarkIoAdapter extends VanillaIoAdapter {

	private StopWatch _watch;
	private static String _logFileName = "db4o-benchmark.log";
	private PrintStream _out; 
	
	public BenchmarkIoAdapter(IoAdapter delegateAdapter) {
		this(delegateAdapter, _logFileName);
	}
	
	public BenchmarkIoAdapter(IoAdapter delegateAdapter, String logFileName) {
		super(delegateAdapter);
		setUp(_logFileName);
	}
	
	public BenchmarkIoAdapter(IoAdapter delegateAdapter, String path, boolean lockFile, long initialLength)throws Db4oIOException {
		this(delegateAdapter, path, lockFile, initialLength, _logFileName);
	}
	
	public BenchmarkIoAdapter(IoAdapter delegateAdapter, String path, boolean lockFile, long initialLength, String logFileName)throws Db4oIOException {
		super(delegateAdapter.open(path, lockFile, initialLength, false));
		setUp(_logFileName);
	}
	
	private void setUp(String logFileName) {
		_logFileName = logFileName;
		_watch = new StopWatch();
		try {
			_out = new PrintStream(new FileOutputStream(_logFileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public IoAdapter open(String path, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
		return new BenchmarkIoAdapter(_delegate, path, lockFile, initialLength, _logFileName);
	}
	
	public void close() throws Db4oIOException {
		super.close();
		_out.flush();
		_out.close();
	}
	
	public int read(byte[] bytes, int length) throws Db4oIOException {
    	int bytesRead;
		_watch.start();
        bytesRead = _delegate.read(bytes, length);
        _watch.stop();
        _out.println(LogConstants.READ_ENTRY + _watch.elapsed() + LogConstants.SEPARATOR + bytesRead); 
        return bytesRead;
    }
	
    public void seek(long pos) throws Db4oIOException {
    	_watch.start();
        _delegate.seek(pos);
        _watch.stop();
        _out.println(LogConstants.SEEK_ENTRY + _watch.elapsed());
    }

    public void sync() throws Db4oIOException {
    	_watch.start();
        _delegate.sync();
        _watch.stop();
        _out.println(LogConstants.SYNC_ENTRY + _watch.elapsed());
    }
    
    public void write(byte[] buffer, int length) throws Db4oIOException {
    	_watch.start();
        _delegate.write(buffer, length);
        _watch.stop();
        _out.println(LogConstants.WRITE_ENTRY + _watch.elapsed() + LogConstants.SEPARATOR + length);
    }
	

}
