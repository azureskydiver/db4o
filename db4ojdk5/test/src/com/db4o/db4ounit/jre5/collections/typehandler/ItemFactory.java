/* Copyright (C) 2008   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections.typehandler;


public interface ItemFactory {
	
	String LIST_FIELD_NAME = "_list";
	
	Object newItem();
	Class itemClass();
	Class listClass();
}
