/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o;

/**
 * @exclude
 */
public class IDHandler extends PrimitiveIntHandler {

	public IDHandler(YapStream stream) {
		super(stream);
	}

    public void defragIndexEntry(ReaderPair readers) {
    	readers.copyID(true,false);
    }

}
