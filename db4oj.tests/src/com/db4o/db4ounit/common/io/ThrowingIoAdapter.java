package com.db4o.db4ounit.common.io;

import com.db4o.ext.*;
import com.db4o.io.*;


/**
 * @decaf.ignore
 */
public class ThrowingIoAdapter extends VanillaIoAdapter {

	private final ThrowCondition _condition;
	private long _pos;
	
	public ThrowingIoAdapter(IoAdapter delegateAdapter, ThrowCondition condition) {
		super(delegateAdapter);
		_condition = condition;
		_pos = 0;
	}

	public IoAdapter open(String path, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
		return new ThrowingIoAdapter(_delegate.open(path, lockFile, initialLength, readOnly), _condition);
	}

	public void seek(long pos) throws Db4oIOException {
		super.seek(pos);
		_pos = pos;
	}
	
	public void write(byte[] buffer, int length) throws Db4oIOException {
		if(_condition.shallThrow(_pos, length)) {
			throw new Db4oIOException("FAIL");
		}
		_delegate.write(buffer, length);
	}

}
