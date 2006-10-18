/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;

public interface SlotReader {

	int offset();

	void offset(int offset);
	void incrementOffset(int numBytes);
	void incrementIntSize();

	void readBegin(byte identifier);
	void readEnd();

	byte readByte();
	void append(byte value);

	int readInt();
	void writeInt(int value);

	long readLong();
	void writeLong(long value);

	public BitMap4 readBitMap(int bitCount);
	
	void copyBytes(byte[] target,int sourceOffset,int targetOffset,int length);
}