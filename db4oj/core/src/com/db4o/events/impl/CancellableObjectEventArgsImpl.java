package com.db4o.events.impl;

public class CancellableObjectEventArgsImpl extends CancellableEventArgsImpl implements com.db4o.events.CancellableObjectEventArgs {

	private Object _subject;
	
	public CancellableObjectEventArgsImpl(Object subject) {
		_subject = subject;
	}

	public Object subject() {
		return _subject;
	}

}
