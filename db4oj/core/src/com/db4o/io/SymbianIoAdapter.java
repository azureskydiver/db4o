package com.db4o.io;

import java.io.*;

/**
 * Workaround for two Symbian/Epoc I/O bugs:
 * - seek() cannot move beyond the current file length.
 *   Fix: Write padding bytes up to the seek target if necessary
 * - Under certain (rare) conditions, calls to RAF.length() seems
 *   to garble up following reads.
 *   Fix: Use a second RAF handle to the file for length() calls
 *   only.
 *   
 * TODO: 
 * - BasicClusterTest C/S fails (in AllTests context only)
 * 
 * @exclude
 */
public class SymbianIoAdapter extends RandomAccessFileAdapter {
    private byte[] _seekBytes=new byte[500];
    private String _path;
    private long _pos;
    private long _length;
    
	protected SymbianIoAdapter(String path, boolean lockFile, long initialLength) throws IOException {
		super(path, lockFile, initialLength);		
		_path=path;
		_pos=0;
		setLength();
	}
	
	private void setLength() throws IOException {
		_length=retrieveLength();
	}

		
	private long retrieveLength() throws IOException {
		RandomAccessFile file=new RandomAccessFile(_path,"r");
		try {
			return file.length();
		} catch (IOException exc) {
			file.close();
			throw exc;
		}
	}
	
	
	public SymbianIoAdapter() {
		super();
	}

	public IoAdapter open(String path, boolean lockFile, long initialLength) throws IOException {
		return new SymbianIoAdapter(path,lockFile,initialLength);
	}

	public long getLength() throws IOException {
		setLength();
		return _length;
	}
	
	public int read(byte[] bytes, int length) throws IOException {
		int ret=super.read(bytes, length);
		_pos+=ret;
		return ret;
	}
	
	public void write(byte[] buffer, int length) throws IOException {
		super.write(buffer, length);		
		_pos+=length;
		if(_pos>_length) {
			setLength();
		}
	}
	
	public void seek(long pos) throws IOException {
		if (pos > _length) {
			setLength();
		}
		if (pos > _length) {
			int len = (int) (pos - _length);
			super.seek(_length);
			_pos=_length;
			if (len < 500) {
				write(_seekBytes, len);
			} else {
				write(new byte[len]);
			}
		}
		super.seek(pos);
		_pos=pos;
	}
}