/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

interface ReadWriteable extends Readable{
	void write(YapWriter a_writer);
}
