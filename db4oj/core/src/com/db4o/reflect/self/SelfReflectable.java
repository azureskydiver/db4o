/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.reflect.self;


public interface SelfReflectable {
	String[] db4o$getFieldNames();
	Class db4o$getFieldType(String fieldName);
}
