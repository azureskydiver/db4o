/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.io;

import com.db4o.io.*;

import db4ounit.*;

public class StorageTest extends StorageTestUnitBase  {
	
	public static void main(String[] args) {
		new ConsoleTestRunner(StorageTest.class).run();
	}
	
	public void testReadWrite() throws Exception {
		int count = 1024 * 8 + 10;
        byte[] data = new byte[count];
        for (int i = 0; i < count; ++i) {
        	data[i] = (byte) (i % 256);
        }
        _storage.write(0, data, data.length);
        _storage.sync();
        
        byte[] readBytes = new byte[count];
        _storage.read(0, readBytes, readBytes.length);
        for (int i = 0; i < count; i++) {
        	Assert.areEqual(data[i], readBytes[i]);
        }
	}
	
	public void testHugeFile() {
		final int dataSize = 1024 * 2;
		final byte[] data = newDataArray(dataSize);
		for (int i=0; i<64; ++i) {
			_storage.write(i * data.length, data, data.length);
		}
		
		final byte[] readBuffer = new byte[dataSize];
		for (int i=0; i<64; ++i) {
			_storage.read(dataSize * (63-i), readBuffer, readBuffer.length);
			ArrayAssert.areEqual(data, readBuffer);
		}
		
	}

	public void testSeek() throws Exception {
		final int count = 1024 * 2 + 10;
        final byte[] data = newDataArray(count);
        _storage.write(0, data, data.length);
        final byte[] readBytes = new byte[count];
        _storage.read(0, readBytes, readBytes.length);
        for (int i = 0; i < count; i++) {
        	Assert.areEqual(data[i], readBytes[i]);
        }
        _storage.read(20, readBytes, readBytes.length);
        for (int i = 0; i < count - 20; i++) {
        	Assert.areEqual(data[i + 20], readBytes[i]);
        }
        
        byte[] writtenData = new byte[10];
        for (int i = 0; i < writtenData.length; ++i) {
        	writtenData[i] = (byte) i;
        }
        _storage.write(1000, writtenData, writtenData.length);
        int readCount = _storage.read(1000, readBytes, 10);
        Assert.areEqual(10, readCount);
        for (int i = 0; i < readCount; ++i) {
        	Assert.areEqual(i, readBytes[i]);
        }
	}

	private byte[] newDataArray(final int count) {
	    final byte[] data = new byte[count];
        for (int i = 0; i < data.length; ++i) {
        	data[i] = (byte) (i % 256);
        }
	    return data;
    }

	public void testReadWriteBytes() throws Exception {
		String[] strs = {
				"short string",
				"this is a really long string, just to make sure that all IoAdapters work correctly. " };
		for(int j = 0; j < strs.length; j++) {
			assertReadWriteString(_storage, strs[j]);
		}
	}
	
	private void assertReadWriteString(Storage adapter, String str) throws Exception {
		byte[] data = str.getBytes();
		byte[] read = new byte[2048];
		adapter.write(0, data, data.length);
		adapter.read(0, read, read.length);
		Assert.areEqual(str, new String(read, 0, data.length));
	}

	/**
	 * @sharpen.rename _testReadWriteAheadFileEnd
	 */
	public void testReadWriteAheadFileEnd() throws Exception {
		String str = "this is a really long string, just to make sure that all IoAdapters work correctly. ";
		assertReadWriteAheadFileEnd(_storage, str);
	}
	
	private void assertReadWriteAheadFileEnd(Storage adapter, String str) throws Exception {
		byte[] data = str.getBytes();
		byte[] read = new byte[2048];
		int readBytes = adapter.read(10, data, data.length);
		Assert.areEqual(-1, readBytes);
		Assert.areEqual(0, adapter.length());
		readBytes = adapter.read(0, data, data.length);
		Assert.areEqual(-1, readBytes);
		Assert.areEqual(0, adapter.length());
		
		adapter.write(10, data, data.length);
		Assert.areEqual(10 + data.length, adapter.length());
		
		
		readBytes = adapter.read(0, read, read.length);
		Assert.areEqual(10 + data.length, readBytes);
		
		readBytes = adapter.read(20 + data.length, read, read.length);
		Assert.areEqual(-1, readBytes);
		
		readBytes = adapter.read(1024 + data.length, read, read.length);
		Assert.areEqual(-1, readBytes);
		
		adapter.write(1200, data, data.length);
		readBytes = adapter.read(0, read, read.length);
		Assert.areEqual(1200 + data.length, readBytes);		
	}

}
