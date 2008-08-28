/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

/**
 * @decaf.ignore.jdk11
 * @sharpen.ignore
 */
class ActiveObjectReference extends java.lang.ref.WeakReference{
	
	Object _referent;
	
	ActiveObjectReference(Object queue, Object objectReference, Object obj){
		super(obj, (java.lang.ref.ReferenceQueue)queue) ;
		_referent = objectReference;
	}
}
