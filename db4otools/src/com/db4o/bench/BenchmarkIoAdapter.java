/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.io.*;


public class BenchmarkIoAdapter extends VanillaIoAdapter {

	private static String _logFileName = "db4o-benchmark.log";
	
	private final StopWatch _watch;
	
	private final BenchmarkStatistics _readStats;
	private final BenchmarkStatistics _writeStats;
	private final BenchmarkStatistics _seekStats;
	private final BenchmarkStatistics _syncStats;
	
	private final int _iterations;
	
	public BenchmarkIoAdapter(IoAdapter delegateAdapter, String logFileName, int iterations) {
		super(delegateAdapter);
		_iterations = iterations;
		_logFileName = logFileName;
		_watch = new StopWatch();
		_readStats = new BenchmarkStatistics(LogConstants.READ_ENTRY, iterations);
		_writeStats = new BenchmarkStatistics(LogConstants.WRITE_ENTRY, iterations);
		_seekStats = new BenchmarkStatistics(LogConstants.SEEK_ENTRY, iterations);
		_syncStats = new BenchmarkStatistics(LogConstants.SYNC_ENTRY, iterations);
	}
	
	public IoAdapter open(String path, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
		return new BenchmarkIoAdapter(_delegate.open(path, lockFile, initialLength, false),_logFileName, _iterations);
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
    	int bytesRead = 0;
		_watch.start();
		for (int i = 0; i < _iterations; i++) {
	        bytesRead = _delegate.read(bytes, length);
		}
        _watch.stop();
        _readStats.log(_watch.elapsed(), bytesRead); 
        return bytesRead;
    }
	
    public void seek(long pos) throws Db4oIOException {
    	_watch.start();
    	for (int i = 0; i < _iterations; i++) {
    		_delegate.seek(pos);
    	}
        _watch.stop();
        _seekStats.log(_watch.elapsed());
    }

    public void sync() throws Db4oIOException {
    	_watch.start();
    	for (int i = 0; i < _iterations; i++) {
    		_delegate.sync();
    	}
        _watch.stop();
        _syncStats.log(_watch.elapsed());
    }
    
    public void write(byte[] buffer, int length) throws Db4oIOException {
    	_watch.start();
    	for (int i = 0; i < _iterations; i++) {
    		_delegate.write(buffer, length);
    	}
        _watch.stop();
        _writeStats.log(_watch.elapsed(), length);
    }

}
