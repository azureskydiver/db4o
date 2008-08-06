/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.typehandlers;



/**
 * marker interface to mark TypeHandlers that marshall
 * objects to the parent slot and do not create objects
 * with own identity.
 */
public interface EmbeddedTypeHandler extends TypeHandler4{

}
