/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.foundation.*;


/**
 * @exclude
 */
public interface SlotBuffer {

	int offset();

	void seek(int offset);
	
	void incrementOffset(int numBytes);
	void incrementIntSize();

	void readBegin(byte identifier);
	void readEnd();

	byte readByte();
	void writeByte(byte value);

	int readInt();
	void writeInt(int value);

	long readLong();
	void writeLong(long value);

	public BitMap4 readBitMap(int bitCount);

	int length();

	void readBytes(byte[] bytes);
}