/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.fieldhandlers.*;
import com.db4o.marshall.*;


/**
 * @exclude
 * TODO: Not all TypeHandlers can implement Comparable4.
 * Consider to change the hierarchy, not to extend Comparable4
 * and to have callers check, if Comparable4 is implemented by 
 * a TypeHandler.
 */
public interface TypeHandler4 extends FieldHandler, Comparable4 {
	
	void delete(DeleteContext context) throws Db4oIOException;
	
	void defragment(DefragmentContext context);

	Object read(ReadContext context);
	
    void write(WriteContext context, Object obj);
	
}
