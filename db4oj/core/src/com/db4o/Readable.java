/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;


/**
 * 
 */
interface Readable {
	Object read(YapReader a_reader);
	int byteCount();
}
