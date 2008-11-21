package com.db4o.db4ounit.common.io;

import com.db4o.io.*;

import db4ounit.mocking.*;

public class MockStorage extends MethodCallRecorder implements Storage {

	private int _returnValue;

	public void close() {
		record("close");
	}

	public long length() {
		record("length");
		return _returnValue;
	}

	private void record(final String methodName) {
	    record(new MethodCall(methodName));
    }

	public int read(long position, byte[] buffer, int bytesToRead) {
		record(new MethodCall("read", position, buffer, bytesToRead));
		return _returnValue;
	}

	public void sync() {
		record("sync");
	}

	public void write(long position, byte[] bytes, int bytesToWrite) {
		record(new MethodCall("write", position, bytes, bytesToWrite));
	}

	public void returnValueForNextCall(int value) {
		_returnValue = value;
    }

}
