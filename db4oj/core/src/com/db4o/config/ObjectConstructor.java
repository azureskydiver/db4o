/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.config;

import com.db4o.*;

/**
 * interface to allow instantiating objects by calling specific constructors.
 * <br><br><b>Examples: ../com/db4o/samples/translators.</b><br><br>
 * By writing classes that implement this interface, it is possible to
 * define which constructor is to be used during the instantiation of a stored object.
 * <br><br>
 * Before starting a db4o session, translator classes that implement the 
 * <code>ObjectConstructor</code> or 
 * <a href="ObjectTranslator.html"><code>ObjectTranslator</code></a>
 * need to be registered.<br><br>
 * Example:<br>
 * <code>
 * Configuration config = Db4o.configure();<br>
 * ObjectClass oc = config.objectClass("package.className");<br>
 * oc.translate(new FooTranslator());</code><br><br>
 */
public interface ObjectConstructor extends ObjectTranslator {

	/**
	 * db4o calls this method when a stored object needs to be instantiated.
	 * <br><br>
	 * @param container the ObjectContainer used
	 * @param storedObject the object stored with 
	 * <a href="ObjectTranslator.html#onStore(com.db4o.ObjectContainer, java.lang.Object)">
	 * <code>ObjectTranslator.html#onStore()</code></a>.
	 * @return the instantiated object.
	 */
	public Object onInstantiate(ObjectContainer container, Object storedObject);
	
}