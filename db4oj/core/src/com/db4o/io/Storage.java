package com.db4o.io;

public interface Storage {

	long length();

	int read(long position, byte[] buffer, int bytesToRead);
	
	void write(long position, byte[] bytes, int bytesToWrite);
	
	void sync();

	void close();

}
