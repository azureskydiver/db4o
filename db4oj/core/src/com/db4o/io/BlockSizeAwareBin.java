/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.io;

/**
 * implement with adapters that need to know
 * about the db4o database file blocksize. 
 */
public interface BlockSizeAwareBin extends Bin {
	
	/**
	 * setter method that gets passed the blocksize.
	 */
	public void blockSize(int blockSize);
	
}
