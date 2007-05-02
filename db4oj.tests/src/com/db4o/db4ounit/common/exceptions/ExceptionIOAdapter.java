/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.exceptions;

import java.io.*;

import com.db4o.io.*;

public class ExceptionIOAdapter extends IoAdapter {

	private IoAdapter _delegate = new RandomAccessFileAdapter();

	public static boolean exception = false;

	public ExceptionIOAdapter() {

	}
	
	protected ExceptionIOAdapter(String path, boolean lockFile,
			long initialLength) throws IOException {
		_delegate = _delegate.open(path, lockFile,
				initialLength);
	}
	
	public void close() throws IOException {
		if (exception) {
			throw new IOException();
		} else {
			_delegate.close();
		}
	}

	public void delete(String path) {
		if (exception) {
			return;
		} else {
			_delegate.delete(path);
		}
	}

	public boolean exists(String path) {
		if (exception) {
			return false;
		} else {
			return _delegate.exists(path);
		}
	}

	public long getLength() throws IOException {
		if (exception) {
			throw new IOException();
		} else {
			return _delegate.getLength();
		}
	}

	public IoAdapter open(String path, boolean lockFile, long initialLength)
			throws IOException {
		return new ExceptionIOAdapter(path, lockFile, initialLength);
	}

	public int read(byte[] bytes, int length) throws IOException {
		if (exception) {
			throw new IOException();
		} else {
			return _delegate.read(bytes, length);
		}
	}

	public void seek(long pos) throws IOException {
		if (exception) {
			throw new IOException();
		} else {
			_delegate.seek(pos);
		}
	}

	public void sync() throws IOException {
		if (exception) {
			throw new IOException();
		} else {
			_delegate.sync();
		}
	}

	public void write(byte[] buffer, int length) throws IOException {
		if (exception) {
			throw new IOException();
		} else {
			_delegate.write(buffer, length);
		}
	}

}
