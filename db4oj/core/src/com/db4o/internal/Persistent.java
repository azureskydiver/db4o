/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

/**
 * @exclude
 */
public interface Persistent {

	byte getIdentifier();

	int ownLength();

	void readThis(Transaction trans, BufferImpl reader);

	void writeThis(Transaction trans, BufferImpl writer);

}