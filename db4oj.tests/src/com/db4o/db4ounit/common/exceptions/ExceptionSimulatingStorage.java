/* Copyright (C) 2007   Versant Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.exceptions;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.io.*;

public class ExceptionSimulatingStorage extends StorageDecorator {

	private IoAdapter _delegate = new RandomAccessFileAdapter();
	private final ExceptionFactory _exceptionFactory;

	private final BooleanByRef _triggersException = new BooleanByRef(false);
	

	public ExceptionSimulatingStorage(Storage storage, ExceptionFactory exceptionFactory) {
		super(storage);
		_exceptionFactory = exceptionFactory;
	}
	
	public void delete(String path) {
		if (triggersException()) {
			return;
		} 
		_delegate.delete(path);
	}

	public boolean exists(String path) {
		if (triggersException()) {
			return false;
		} else {
			return _delegate.exists(path);
		}
	}

	@Override
	protected Bin decorate(Bin bin) {
		return new ExceptionSimulatingBin(bin, _exceptionFactory, _triggersException);
	}

	public void triggerException(boolean exception) {
		this._triggersException.value = exception;
	}

	public boolean triggersException() {
		return this._triggersException.value;
	}

	static class ExceptionSimulatingBin extends BinDecorator {

		private final ExceptionFactory _exceptionFactory;
		private final BooleanByRef _triggersException;
		
		public ExceptionSimulatingBin(Bin bin, ExceptionFactory exceptionFactory, BooleanByRef triggersException) {
			super(bin);
			_exceptionFactory = exceptionFactory;
			_triggersException = triggersException;
		}

		public int read(long pos, byte[] bytes, int length) throws Db4oIOException {
			if (triggersException()) {
				_exceptionFactory.throwException();
				return 0;
			} else {
				return _bin.read(pos, bytes, length);
			}
		}

		public void sync() throws Db4oIOException {
			if (triggersException()) {
				_exceptionFactory.throwException();
			} else {
				_bin.sync();
			}
		}

		public void write(long pos, byte[] buffer, int length) throws Db4oIOException {
			if (triggersException()) {
				_exceptionFactory.throwException();
			} else {
				_bin.write(pos, buffer, length);
			}
		}

		public void close() throws Db4oIOException {
			if (triggersException()) {
				_exceptionFactory.throwException();
			} else {
				_bin.close();
			}
		}

		public long length() throws Db4oIOException {
			if (triggersException()) {
				_exceptionFactory.throwException();
				return 0;
			} else {
				return _bin.length();
			}
		}
		
		private boolean triggersException() {
			return _triggersException.value;
		}
	}
}
