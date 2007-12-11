/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;



/**
 * @exclude
 */
public interface Readable {
	Object read(BufferImpl a_reader);
	int marshalledLength();
}
