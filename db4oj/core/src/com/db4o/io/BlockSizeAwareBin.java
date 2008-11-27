/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.io;

/**
 * @exclude
 */
public interface BlockSizeAwareBin extends Bin {
	
	public void blockSize(int blockSize);
	
}
