/**
 * 
 */
package com.db4o.io;

public interface Bin {

	long length();

	int read(long position, byte[] bytes, int bytesToRead);
	
	void write(long position, byte[] bytes, int bytesToWrite);
	
	void sync();

	void close();

}