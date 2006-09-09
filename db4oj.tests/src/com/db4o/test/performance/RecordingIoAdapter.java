/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.performance;

import java.io.*;

import com.db4o.io.*;

/**
 * IO adapter for benchmark.
 * @exclude
 */
public class RecordingIoAdapter extends VanillaIoAdapter {
	// NOTE/FIXME: Ugly hack to prevent YapRandomAccessFile timer file handle from
	// writing asonchronously to our log file. Very fragile, basically YapRandomAccessFile
	// timer handles - or IoAdapter design ;P - needs to be fixed.
	private int _runningId;
	
	private String _logPath;

	private RandomAccessFile _writer;

	private long _pos;
	
	private int _runs;

	public RecordingIoAdapter(IoAdapter adapter, String logPath) {
		super(adapter);
		_logPath = logPath;
		_runningId=0;
	}

	protected RecordingIoAdapter(IoAdapter adapter, String logPath,
			String file, boolean append, long initialLength) throws IOException {
		super(adapter, file, append, initialLength);
		_writer = new RandomAccessFile(logPath, "rw");
		_runs=0;
	}

	public void close() throws IOException {
		super.close();
		writeLogChar('q');
		//System.err.println(_runs);
	}

	public IoAdapter open(String path, boolean lockFile, long initialLength)
			throws IOException {
		_runningId++;
		return new RecordingIoAdapter(_delegate, _logPath+"."+_runningId, path, lockFile,
				initialLength);
	}

	public int read(byte[] buffer, int length) throws IOException {
		writeLog('r',_pos,length);
		return super.read(buffer,length);

	}

	public void seek(long pos) throws IOException {
		_pos = pos;
		super.seek(pos);
	}

	public void write(byte[] buffer, int length) throws IOException {
		writeLog('w',_pos,length);
		super.write(buffer,length);
	}
	
	public void sync() throws IOException {
		writeLogChar('f');
		super.sync();
	}
	
	private void writeLog(char type,long pos,int length) throws IOException {
		_writer.writeChar(type);
		_writer.writeLong(pos);
		_writer.writeInt(length);
		_runs++;
	}
	
	private void writeLogChar(char c) throws IOException {
		_writer.writeChar(c);
		_runs++;
	}
}