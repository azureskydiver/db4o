package com.db4o.io;

public class BinDecorator implements Bin {

	protected final Bin _bin;

	public BinDecorator(Bin bin) {
		_bin = bin;
	}

	public void close() {
    	_bin.close();
    }

	public long length() {
		return _bin.length();
    }

	public int read(long position, byte[] buffer, int bytesToRead) {
    	return _bin.read(position, buffer, bytesToRead);
    }

	public void sync() {
		_bin.sync();
    }

	public void write(long position, byte[] bytes, int bytesToWrite) {
    	_bin.write(position, bytes, bytesToWrite);
    }

}