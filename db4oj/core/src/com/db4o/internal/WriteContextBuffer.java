/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;


/**
 * @exclude
 */
public class WriteContextBuffer extends Buffer {
	
	public WriteContextBuffer(int length) {
		super(length);
	}
	
	protected boolean canWritePersistentBase(){
		return false;
	}

}
