/* Copyright (C) 2009  db4objects Inc.   http://www.db4o.com */

package com.db4o.io;

/**
 * Strategy for file/byte array growth by a constant factor
 */
public class ConstantGrowthStrategy implements GrowthStrategy {	
	private final int _growth;
	
	/**
	 * @param growth The constant growth size
	 */
	public ConstantGrowthStrategy(int growth) {
		_growth = growth;
	}
	
	public long newSize(long curSize) {
		return curSize + _growth;
	}
}
