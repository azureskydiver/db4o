/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.events;

public class ObjectEventArgs extends EventArgs {
	
	private Object _obj;

	public ObjectEventArgs(Object obj) {
		_obj = obj;
	}

	public Object object() {
		return _obj;
	}
}
