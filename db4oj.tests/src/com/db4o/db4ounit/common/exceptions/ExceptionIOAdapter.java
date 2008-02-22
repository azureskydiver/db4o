/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.exceptions;

import com.db4o.ext.*;
import com.db4o.io.*;

public class ExceptionIOAdapter extends IoAdapter {

	private IoAdapter _delegate = new RandomAccessFileAdapter();

	public static boolean exception = false;

	public ExceptionIOAdapter() {

	}
	
	protected ExceptionIOAdapter(String path, boolean lockFile,
			long initialLength) throws Db4oIOException {
		_delegate = _delegate.open(path, lockFile, initialLength, false);
	}
	
	public void close() throws Db4oIOException {
		if (exception) {
			throw new Db4oIOException();
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

	public long getLength() throws Db4oIOException {
		if (exception) {
			throw new Db4oIOException();
		} else {
			return _delegate.getLength();
		}
	}

	public IoAdapter open(String path, boolean lockFile, long initialLength, boolean readOnly)
			throws Db4oIOException {
		return new ExceptionIOAdapter(path, lockFile, initialLength);
	}

	public int read(byte[] bytes, int length) throws Db4oIOException {
		if (exception) {
			throw new Db4oIOException();
		} else {
			return _delegate.read(bytes, length);
		}
	}

	public void seek(long pos) throws Db4oIOException {
		if (exception) {
			throw new Db4oIOException();
		} else {
			_delegate.seek(pos);
		}
	}

	public void sync() throws Db4oIOException {
		if (exception) {
			throw new Db4oIOException();
		} else {
			_delegate.sync();
		}
	}

	public void write(byte[] buffer, int length) throws Db4oIOException {
		if (exception) {
			throw new Db4oIOException();
		} else {
			_delegate.write(buffer, length);
		}
	}

}
