/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.typehandlers;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.fieldhandlers.*;
import com.db4o.marshall.*;


/**
 * handles reading, writing, deleting, defragmenting and 
 * comparisons for types of objects.<br><br>
 * Custom Typehandlers can be implemented to alter the default 
 * behaviour of storing all non-transient fields of an object.<br><br>
 * @see {@link Configuration#registerTypeHandler(com.db4o.typehandlers.TypeHandlerPredicate, TypeHandler4)} 
 */

// 	TODO: Not all TypeHandlers can implement Comparable4.
// Consider to change the hierarchy, not to extend Comparable4
// and to have callers check, if Comparable4 is implemented by 
// a TypeHandler.
public interface TypeHandler4 extends FieldHandler, Comparable4 {
	
	/**
	 * gets called when an object gets deleted.
	 * @param context 
	 * @throws Db4oIOException
	 */
	void delete(DeleteContext context) throws Db4oIOException;
	
	/**
	 * gets called when an object gets defragmented.
	 * @param context
	 */
	void defragment(DefragmentContext context);

	/**
	 * gets called when an object is read from the database.
	 * @param context
	 * @return the instantiated object
	 */
	Object read(ReadContext context);
	
	/**
	 * gets called when an object is to be written to the database.
	 * @param context
	 * @param obj the object
	 */
    void write(WriteContext context, Object obj);
	
}
