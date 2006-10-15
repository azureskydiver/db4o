/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o;

public interface SlotReader {

	int offset();

	void offset(int offset);
	void incrementOffset(int numBytes);
	void incrementIntSize();

	void readBegin(byte identifier);

	byte readByte();
	void append(byte value);

	int readInt();
	void writeInt(int value);

	long readLong();
	void writeLong(long value);
}