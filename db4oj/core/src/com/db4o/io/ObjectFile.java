package com.db4o.io;

import java.io.IOException;

public abstract class ObjectFile {
	private int _blockSize;
	
	public void blockSeek(int address) throws IOException {
		blockSeek(address,0);
	}

	public void blockSeek(int address, int newAddressOffset)
			throws IOException {		
		regularSeek((long)address*_blockSize+newAddressOffset);
	}

	public void blockSize(int blockSize) {
		_blockSize=blockSize;
	}

	public abstract void close() throws IOException;

	public abstract long length() throws IOException;

	public abstract void lock();

	public int read(byte[] buffer) throws IOException {
		return read(buffer,buffer.length);		
	}

	public abstract int read(byte[] bytes, int length) throws IOException;

	public abstract void regularSeek(long pos) throws IOException;

	public abstract void sync() throws IOException;

	public abstract void unlock();

	public void write(byte[] bytes) throws IOException {
		write(bytes,bytes.length);
	}

	public abstract void write(byte[] buffer, int length) throws IOException;
	
	public int blockSize() {
		return _blockSize;
	}
}