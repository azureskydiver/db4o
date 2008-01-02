/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.internal.handlers.*;


/**
 * @exclude
 */
public class IDHandler extends IntHandler {

	public IDHandler(ObjectContainerBase stream) {
		super(stream);
	}

    public void defragIndexEntry(DefragmentContextImpl context) {
    	context.copyID(true,false);
    }

}
