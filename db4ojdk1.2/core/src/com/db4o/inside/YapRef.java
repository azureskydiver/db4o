/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside;

class YapRef extends java.lang.ref.WeakReference{
	
	Object _referent;
	
	YapRef(Object queue, Object objectReference, Object obj){
		super(obj, (java.lang.ref.ReferenceQueue)queue) ;
		_referent = objectReference;
	}
}
