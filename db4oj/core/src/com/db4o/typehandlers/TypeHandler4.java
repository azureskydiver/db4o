/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.typehandlers;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;


/**
 * handles reading, writing, deleting, defragmenting and 
 * comparisons for types of objects.<br><br>
 * Custom Typehandlers can be implemented to alter the default 
 * behaviour of storing all non-transient fields of an object.<br><br>
 * @see {@link Configuration#registerTypeHandler(com.db4o.typehandlers.TypeHandlerPredicate, TypeHandler4)} 
 */
public interface TypeHandler4 {
	
	
	
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
	 * gets called when an object is to be written to the database.

	 * @param context
	 * @param obj the object
	 */
    void write(WriteContext context, Object obj);

    /**
	 * gets called to check whether a TypeHandler can hold
	 * a specific type
	 * @param type the type
	 * @return true, if this Typehandler can hold a type
	 */
	boolean canHold(ReflectClass type);
	
}
