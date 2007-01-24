/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.io;

import java.io.File;
import java.io.IOException;

import com.db4o.io.CachedIoAdapter;
import com.db4o.io.IoAdapter;
import com.db4o.io.RandomAccessFileAdapter;

import db4ounit.Assert;
import db4ounit.TestCase;
import db4ounit.TestLifeCycle;

public class CachedIoAdaptorTest implements TestCase, TestLifeCycle {
	private IoAdapter _adaptor;

	private String _fileName = "CachedIoAdaptorTest.dat";

	public void setUp() throws Exception {
		
	}
	
	public void tearDown() throws Exception {
		_adaptor.close();
		new File(_fileName).delete();
	}

	public void initCachedRandomAccessAdapter() throws Exception {
		new File(_fileName).delete();
		_adaptor = new CachedIoAdapter(new RandomAccessFileAdapter());
		_adaptor = _adaptor.open(_fileName, false, 0);
	}
	
	public void testReadWrite() throws Exception {
		initCachedRandomAccessAdapter();
		assertReadWrite();
	}

	private void assertReadWrite() throws IOException {
		int count = 1024 * 8 + 10;
		byte[] data = new byte[count];
		for (int i = 0; i < count; ++i) {
			data[i] = (byte) (i % 256);
		}
		_adaptor.write(data);
		_adaptor.seek(0);
		byte[] readBytes = new byte[count];
		_adaptor.read(readBytes);
		for (int i = 0; i < count; i++) {
			Assert.areEqual(data[i], readBytes[i]);
		}

		_adaptor.seek(10);
		_adaptor.read(readBytes);
		for (int i = 0; i < count - 10; i++) {
			Assert.areEqual(data[i + 10], readBytes[i]);
		}
	}

}
