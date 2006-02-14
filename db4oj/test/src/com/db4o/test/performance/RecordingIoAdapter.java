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
	private static boolean ALREADY_ACTIVE=false;
	private boolean active;
	private int count;
	
	private String _logPath;

	private DataOutputStream _writer;

	private long _pos;

	public RecordingIoAdapter(IoAdapter adapter, String logPath) {
		super(adapter);
		_logPath = logPath;
	}

	protected RecordingIoAdapter(IoAdapter adapter, String logPath,
			String file, boolean append, long initialLength) throws IOException {
		super(adapter, file, append, initialLength);
		if(!ALREADY_ACTIVE) {
			ALREADY_ACTIVE=true;
			active=true;
			_writer = new DataOutputStream(new FileOutputStream(logPath));
		}
	}

	public void close() throws IOException {
		super.close();
		writeLogChar('q');
		if(active) {
			_writer.close();
		}
	}

	public IoAdapter open(String path, boolean lockFile, long initialLength)
			throws IOException {
		return new RecordingIoAdapter(_delegate, _logPath, path, lockFile,
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
		_writer.flush();
		super.sync();
	}
	
	private void writeLog(char type,long pos,int length) throws IOException {
		if(!active) {
			return;
		}
		_writer.writeChar(type);
		_writer.writeLong(pos);
		_writer.writeInt(length);
		_writer.flush();
		count++;
	}
	
	private void writeLogChar(char c) throws IOException {
		if(!active) {
			return;
		}
		_writer.writeChar(c);
		count++;
	}
}