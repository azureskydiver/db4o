/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.events.impl;

import com.db4o.events.ObjectEventArgs;

/**
 * @exclude
 */
public class ObjectEventArgsImpl implements ObjectEventArgs {
	
	private Object _obj;

	public ObjectEventArgsImpl(Object obj) {
		_obj = obj;
	}

	public Object subject() {
		return _obj;
	}
}
