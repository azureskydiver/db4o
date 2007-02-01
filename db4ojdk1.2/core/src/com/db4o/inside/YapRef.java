/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside;

class YapRef extends java.lang.ref.WeakReference
{
	Object i_yapObject;
	
	YapRef(Object a_queue, Object a_yapObject, Object a_object){
		super(a_object, (java.lang.ref.ReferenceQueue)a_queue) ;
		i_yapObject = a_yapObject;
	}
}
