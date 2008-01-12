/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench;

import java.io.*;
import java.util.*;

import com.db4o.bench.logging.*;
import com.db4o.bench.timing.*;
import com.db4o.ext.*;
import com.db4o.io.*;


public class BenchmarkIoAdapter extends VanillaIoAdapter {

	private static String _logFileName = "db4o-benchmark.log";
	
	private final NanoStopWatch _watch;
	
	private final Set _operations;
	private final int _iterations;
	private final boolean _append;
	
	private final Map _statistics;
	
	public BenchmarkIoAdapter(IoAdapter delegateAdapter, String logFileName, Set operations, int iterations, boolean append) {
		super(delegateAdapter);
		_iterations = iterations;
		_logFileName = logFileName;
		try {
			_watch = new NanoStopWatch();
		} catch (Exception e) {
			throw new Db4oIOException(e.getMessage());
		}

		_append = append;
		_operations = operations;
		
		_statistics = new HashMap();
		Iterator it = operations.iterator();
		while (it.hasNext()) {
			String cmd = (String)it.next();
			BenchmarkStatistics stat = new BenchmarkStatistics(cmd, iterations);
			_statistics.put(cmd, stat);
		}
	}
	
	public BenchmarkIoAdapter(IoAdapter delegateAdapter, String logFileName, int iterations, boolean append) {
		this(delegateAdapter, logFileName, LogConstants.allEntries(), iterations, append);
	}
	
	public IoAdapter open(String path, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
		return new BenchmarkIoAdapter(_delegate.open(path, lockFile, initialLength, false),_logFileName, _operations, _iterations, _append);
	}
	
	
	public void close() throws Db4oIOException {
		_delegate.close();
		outputStatistics();
	}
	
	private void outputStatistics() {
		try {
			PrintStream out = new PrintStream(new FileOutputStream(_logFileName, _append));

			Iterator it = _statistics.values().iterator();
			while (it.hasNext()) {
				((BenchmarkStatistics)it.next()).printStatistics(out);
			}
			
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

        BenchmarkStatistics bms = (BenchmarkStatistics)_statistics.get(LogConstants.READ_ENTRY);
        bms.log(_watch.elapsed(), bytesRead);
        _statistics.put(LogConstants.READ_ENTRY, bms);
        
        return bytesRead;
    }
	
    public void seek(long pos) throws Db4oIOException {
    	_watch.start();
    	for (int i = 0; i < _iterations; i++) {
    		_delegate.seek(pos);
    	}
        _watch.stop();
        
        BenchmarkStatistics bms = (BenchmarkStatistics)_statistics.get(LogConstants.SEEK_ENTRY);
        bms.log(_watch.elapsed());
        _statistics.put(LogConstants.SEEK_ENTRY, bms);
    }

    public void sync() throws Db4oIOException {
    	_watch.start();
    	for (int i = 0; i < _iterations; i++) {
    		_delegate.sync();
    	}
        _watch.stop();
        
        BenchmarkStatistics bms = (BenchmarkStatistics)_statistics.get(LogConstants.SYNC_ENTRY);
        bms.log(_watch.elapsed());
        _statistics.put(LogConstants.SYNC_ENTRY, bms);
    }
    
    public void write(byte[] buffer, int length) throws Db4oIOException {
    	_watch.start();
    	for (int i = 0; i < _iterations; i++) {
    		_delegate.write(buffer, length);
    	}
        _watch.stop();
        
        BenchmarkStatistics bms = (BenchmarkStatistics)_statistics.get(LogConstants.WRITE_ENTRY);
        bms.log(_watch.elapsed(), length);
        _statistics.put(LogConstants.WRITE_ENTRY, bms);
    }

}
