/* Copyright (C) 2008   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections.typehandler;


public abstract class ItemFactory {
	
	static String LIST_FIELD_NAME = "_list";
	
	public abstract Object newItem();
	public abstract Class itemClass();
	public abstract Class listClass();
}
