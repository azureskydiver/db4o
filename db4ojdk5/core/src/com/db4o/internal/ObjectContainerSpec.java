/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;



/**
 * Workaround to provide the Java 5 version with a hook to add ExtObjectContainer.
 * (Generic method declarations won't match ungenerified YapStreamBase implementations
 * otherwise and implementing it directly kills .NET conversion.)
 * 
 * @exclude
 * @decaf.ignore.implements InternalObjectContainer
 */
public interface ObjectContainerSpec extends InternalObjectContainer {
}
