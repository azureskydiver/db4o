/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.io;

import java.io.File;
import java.io.IOException;

import com.db4o.io.CachedIoAdapter;
import com.db4o.io.IoAdapter;
import com.db4o.io.RandomAccessFileAdapter;

import db4ounit.Assert;
import db4ounit.CodeBlock;
import db4ounit.TestCase;
import db4ounit.TestLifeCycle;

public class IoAdapterTest implements TestCase, TestLifeCycle {

	private String _cachedIoAdapterFile = "CachedIoAdapter.dat";

	private String _randomAccessFileAdapterFile = "_randomAccessFileAdapter.dat";

	private IoAdapter[] _adapters;

	public void setUp() throws Exception {
		deleteAllTestFiles();
		initAdapters();
	}

	private void initAdapters() throws Exception {
		_adapters = new IoAdapter[] { initRandomAccessAdapter(),
				initCachedRandomAccessAdapter(), };
	}

	public void tearDown() throws Exception {
		closeAdapters();
		deleteAllTestFiles();
	}

	public void testReadWrite() throws Exception {
		for (int i = 0; i < _adapters.length; ++i) {
			assertReadWrite(_adapters[i]);
		}
	}

	private void assertReadWrite(IoAdapter adapter) throws IOException {
		adapter.seek(0);
		int count = 1024 * 8 + 10;
		byte[] data = new byte[count];
		for (int i = 0; i < count; ++i) {
			data[i] = (byte) (i % 256);
		}
		adapter.write(data);
		adapter.seek(0);
		byte[] readBytes = new byte[count];
		adapter.read(readBytes);
		for (int i = 0; i < count; i++) {
			Assert.areEqual(data[i], readBytes[i]);
		}
	}

	public void testSeek() throws Exception {
		for (int i = 0; i < _adapters.length; ++i) {
			assertSeek(_adapters[i]);
		}
	}

	public void testReadWriteBytes() throws Exception {
		String[] strs = {
				"short string",
				"this is a really long string, just to make sure that all IoAdapters work correctly. " };
		for (int i = 0; i < _adapters.length; i++) {
			for(int j = 0; j < strs.length; j++)
			assertReadWriteString(_adapters[i], strs[j]);
		}
	}
	
	private void assertReadWriteString(IoAdapter adapter, String str) throws Exception {
		byte[] data = str.getBytes();
		byte[] read = new byte[2048];
		adapter.seek(0);
		adapter.write(data);
		adapter.seek(0);
		adapter.read(read);
		Assert.areEqual(str, new String(read, 0, data.length));
	}
	
	public void testReadWriteAheadFileEnd() throws Exception {
		String str = "this is a really long string, just to make sure that all IoAdapters work correctly. ";
		for (int i = 0; i < _adapters.length; i++) {
			assertReadWriteAheadFileEnd(_adapters[i], str);
		}
	}
	
	private void assertReadWriteAheadFileEnd(IoAdapter adapter, String str) throws Exception {
		byte[] data = str.getBytes();
		byte[] read = new byte[2048];
		adapter.seek(10);
		int readBytes = adapter.read(data);
		Assert.areEqual(-1, readBytes);
		Assert.areEqual(0, adapter.getLength());
		adapter.seek(0);
		readBytes = adapter.read(data);
		Assert.areEqual(-1, readBytes);
		Assert.areEqual(0, adapter.getLength());
		
		adapter.seek(10);
		adapter.write(data);
		Assert.areEqual(10 + data.length, adapter.getLength());
		
		
		adapter.seek(0);
		readBytes = adapter.read(read);
		Assert.areEqual(10 + data.length, readBytes);
		
		adapter.seek(20 + data.length);
		readBytes = adapter.read(read);
		Assert.areEqual(-1, readBytes);
		
		adapter.seek(1024 + data.length);
		readBytes = adapter.read(read);
		Assert.areEqual(-1, readBytes);
		
		adapter.seek(1200);
		adapter.write(data);
		adapter.seek(0);
		readBytes = adapter.read(read);
		Assert.areEqual(1200 + data.length, readBytes);		
	}

	public void testReadTooMuch() throws Exception {
		for (int i = 0; i < _adapters.length; i++) {
			assertReadTooMuch(_adapters[i]);
		}
	}
	
	private void assertReadTooMuch(final IoAdapter adapter) throws Exception {
		final byte[] data = "hello".getBytes();
		final byte[] read = new byte[1];
		adapter.seek(0);
		adapter.write(data);
		adapter.seek(0);
		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock () {
			public void run() throws Throwable {
				adapter.read(read, data.length);
			}
		});
	}

	public void testReopen() throws Exception {
		testReadWrite();
		closeAdapters();
		initAdapters();
		testReadWrite();
	}
	
	private void assertSeek(IoAdapter adapter) throws Exception {
		int count = 1024 * 2 + 10;
		byte[] data = new byte[count];
		for (int i = 0; i < data.length; ++i) {
			data[i] = (byte) (i % 256);
		}
		adapter.write(data);
		byte[] readBytes = new byte[count];
		adapter.seek(0);
		adapter.read(readBytes);
		for (int i = 0; i < count; i++) {
			Assert.areEqual(data[i], readBytes[i]);
		}
		adapter.seek(20);
		adapter.read(readBytes);
		for (int i = 0; i < count - 20; i++) {
			Assert.areEqual(data[i + 20], readBytes[i]);
		}

		byte[] writtenData = new byte[10];
		for (int i = 0; i < writtenData.length; ++i) {
			writtenData[i] = (byte) i;
		}
		adapter.seek(1000);
		adapter.write(writtenData);
		adapter.seek(1000);
		int readCount = adapter.read(readBytes, 10);
		Assert.areEqual(10, readCount);
		for (int i = 0; i < readCount; ++i) {
			Assert.areEqual(i, readBytes[i]);
		}
	}

	private IoAdapter initCachedRandomAccessAdapter() throws Exception {
		IoAdapter adapter = new CachedIoAdapter(new RandomAccessFileAdapter());
		adapter = adapter.open(_cachedIoAdapterFile, false, 0);
		return adapter;
	}

	private IoAdapter initRandomAccessAdapter() throws Exception {
		IoAdapter adapter = new RandomAccessFileAdapter();
		adapter = adapter.open(_randomAccessFileAdapterFile, false, 0);
		return adapter;
	}

	private void deleteAllTestFiles() throws Exception {
		new File(_cachedIoAdapterFile).delete();
		new File(_randomAccessFileAdapterFile).delete();
	}

	private void closeAdapters() {
		for (int i = 0; i < _adapters.length; ++i) {
			try {
				_adapters[i].close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

}
