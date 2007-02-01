/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.inside.*;


/**
 * @exclude
 */
public interface Readable {
	Object read(Buffer a_reader);
	int byteCount();
}
