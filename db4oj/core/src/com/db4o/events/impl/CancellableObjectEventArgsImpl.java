/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.events.impl;

/**
 * @exclude
 */
public class CancellableObjectEventArgsImpl extends CancellableEventArgsImpl implements com.db4o.events.CancellableObjectEventArgs {

	private Object _subject;
	
	public CancellableObjectEventArgsImpl(Object subject) {
		_subject = subject;
	}

	public Object object() {
		return _subject;
	}
}
