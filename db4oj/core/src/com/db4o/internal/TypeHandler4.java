/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;
import com.db4o.internal.fieldhandlers.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public interface TypeHandler4 extends Comparable4, FieldHandler {
	
	void delete(DeleteContext context) throws Db4oIOException;
	
	void defragment(DefragmentContext context);

	Object read(ReadContext context);
	
    void write(WriteContext context, Object obj);
	
}
