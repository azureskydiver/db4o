package com.db4o.events.impl;

import com.db4o.events.ObjectEventArgs;

public class ObjectEventArgsImpl implements ObjectEventArgs {
	
	private Object _obj;

	public ObjectEventArgsImpl(Object obj) {
		_obj = obj;
	}

	public Object subject() {
		return _obj;
	}
}
