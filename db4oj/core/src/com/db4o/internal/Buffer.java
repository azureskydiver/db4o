/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.foundation.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public interface Buffer extends ReadBuffer, WriteBuffer{

	void incrementOffset(int numBytes);
	void incrementIntSize();

    int length();
    
	void readBegin(byte identifier);
	void readEnd();

	public BitMap4 readBitMap(int bitCount);

}