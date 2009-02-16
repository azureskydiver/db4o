/* Copyright (C) 2009  db4objects Inc.   http://www.db4o.com */

package com.db4o.io;

/**
 * Strategy for file/byte array growth that will always double the current size
 */
public class DoublingGrowthStrategy implements GrowthStrategy {
	public long newSize(long curSize) {
		return curSize * 2;
	}
}
