/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.io.*;


public class BenchmarkIoAdapter extends VanillaIoAdapter {

	private StopWatch _watch;
	private static String _logFileName = "db4o-benchmark.log";
	
	private BenchmarkStatistics _readStats;
	private BenchmarkStatistics _writeStats;
	private BenchmarkStatistics _seekStats;
	private BenchmarkStatistics _syncStats;
	
	
	public BenchmarkIoAdapter(IoAdapter delegateAdapter) {
		this(delegateAdapter, _logFileName);
	}
	
	public BenchmarkIoAdapter(IoAdapter delegateAdapter, String logFileName) {
		super(delegateAdapter);
		setUp(logFileName);
	}
	
	public BenchmarkIoAdapter(IoAdapter delegateAdapter, String path, boolean lockFile, long initialLength)throws Db4oIOException {
		this(delegateAdapter, path, lockFile, initialLength, _logFileName);
	}
	
	public BenchmarkIoAdapter(IoAdapter delegateAdapter, String path, boolean lockFile, long initialLength, String logFileName)throws Db4oIOException {
		super(delegateAdapter.open(path, lockFile, initialLength, false));
		setUp(logFileName);
	}
	
	private void setUp(String logFileName) {
		_logFileName = logFileName;
		_watch = new StopWatch();
		_readStats = new BenchmarkStatistics(LogConstants.READ_ENTRY);
		_writeStats = new BenchmarkStatistics(LogConstants.WRITE_ENTRY);
		_seekStats = new BenchmarkStatistics(LogConstants.SEEK_ENTRY);
		_syncStats = new BenchmarkStatistics(LogConstants.SYNC_ENTRY);
	}

	public IoAdapter open(String path, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
		return new BenchmarkIoAdapter(_delegate, path, lockFile, initialLength);
	}
	
	public void close() throws Db4oIOException {
		_delegate.close();
		outputStatistics();
	}
	
	private void outputStatistics() {
		try {
			PrintStream out = new PrintStream(new FileOutputStream(_logFileName));
			_readStats.printStatistics(out);
			_writeStats.printStatistics(out);
			_seekStats.printStatistics(out);
			_syncStats.printStatistics(out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			System.err.println("Error opening PrintSream - Cannot write Benchmark log!");
			e.printStackTrace();
		}
	}

	public int read(byte[] bytes, int length) throws Db4oIOException {
    	int bytesRead;
		_watch.start();
        bytesRead = _delegate.read(bytes, length);
        _watch.stop();
        _readStats.log(_watch.elapsed(), bytesRead); 
        return bytesRead;
    }
	
    public void seek(long pos) throws Db4oIOException {
    	_watch.start();
        _delegate.seek(pos);
        _watch.stop();
        _seekStats.log(_watch.elapsed());
    }

    public void sync() throws Db4oIOException {
    	_watch.start();
        _delegate.sync();
        _watch.stop();
        _syncStats.log(_watch.elapsed());
    }
    
    public void write(byte[] buffer, int length) throws Db4oIOException {
    	_watch.start();
        _delegate.write(buffer, length);
        _watch.stop();
        _writeStats.log(_watch.elapsed(), length);
    }
	

}
