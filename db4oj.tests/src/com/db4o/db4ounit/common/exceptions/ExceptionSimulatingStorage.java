/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.exceptions;

import com.db4o.ext.*;
import com.db4o.io.*;

public class ExceptionSimulatingStorage extends StorageDecorator {

	private IoAdapter _delegate = new RandomAccessFileAdapter();

	public static boolean exception = false;

	public ExceptionSimulatingStorage(Storage storage) {
		super(storage);
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

	@Override
	protected Bin decorate(Bin bin) {
		return new ExceptionSimulatingBin(bin);
	}

	static class ExceptionSimulatingBin extends BinDecorator {

		public ExceptionSimulatingBin(Bin bin) {
			super(bin);
		}

		public int read(long pos, byte[] bytes, int length) throws Db4oIOException {
			if (exception) {
				throw new Db4oIOException();
			} else {
				return _bin.read(pos, bytes, length);
			}
		}

		public void sync() throws Db4oIOException {
			if (exception) {
				throw new Db4oIOException();
			} else {
				_bin.sync();
			}
		}

		public void write(long pos, byte[] buffer, int length) throws Db4oIOException {
			if (exception) {
				throw new Db4oIOException();
			} else {
				_bin.write(pos, buffer, length);
			}
		}

		public void close() throws Db4oIOException {
			if (exception) {
				throw new Db4oIOException();
			} else {
				_bin.close();
			}
		}

		public long length() throws Db4oIOException {
			if (exception) {
				throw new Db4oIOException();
			} else {
				return _bin.length();
			}
		}
	}
}
