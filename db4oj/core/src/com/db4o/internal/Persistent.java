/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

/**
 * @exclude
 */
public interface Persistent {

	/**
	 * @moveto new com.db4o.internal.Persistent interface
	 * all four of the following abstract methods  
	 */
	byte getIdentifier();

	int ownLength();

	void readThis(Transaction trans, Buffer reader);

	void writeThis(Transaction trans, Buffer writer);

}