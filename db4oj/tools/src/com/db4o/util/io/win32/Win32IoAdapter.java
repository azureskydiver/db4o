package com.db4o.util.io.win32;

import java.io.IOException;

import com.db4o.io.IoAdapter;

/**
 * @author rodrigob
 *
 */
public class Win32IoAdapter extends IoAdapter {
	
	static {
		System.loadLibrary("Win32IoAdapter");
	}
	
	private long _handle;

	public Win32IoAdapter(String path, boolean lockFile, long initialLength) {
		_handle = openFile(path, lockFile, initialLength);
	}	

	public Win32IoAdapter() {
	}

	public void close() throws IOException {
		closeFile(getHandle());
		_handle = 0;
	}

	public long getLength() throws IOException {
		return getLength(getHandle());
	}

	public IoAdapter open(String path, boolean lockFile, long initialLength)
			throws IOException {
		return new Win32IoAdapter(path, lockFile, initialLength);
	}

	public int read(byte[] bytes, int length) throws IOException {
		return read(getHandle(), bytes, length);
	}

	public void seek(long pos) throws IOException {
		seek(getHandle(), pos);
	}
	
	public void sync() throws IOException {
		sync(getHandle());
	}
	
	public void write(byte[] bytes, int length) throws IOException {
		write(getHandle(), bytes, length);
	}
	
	public void copy(long oldAddress, long newAddress, int length)
			throws IOException {
		copy(getHandle(), oldAddress, newAddress, length);
	}

	private long getHandle() {
		if (0 == _handle) {
			throw new IllegalStateException("File is not open.");
		}
		return _handle;
	}
	
	private static native long openFile(String path, boolean lockFile, long initialLength);
	
	private static native void closeFile(long handle);
	
	private static native long getLength(long handle);
	
	private static native int read(long handle, byte[] bytes, int length);
	
	private static native void seek(long handle, long pos);
	
	private static native void sync(long handle);
	
	private static native void write(long handle, byte[] bytes, int lenght);
	
	private static native void copy(long handle, long oldAddress, long newAddress, int length);
}
