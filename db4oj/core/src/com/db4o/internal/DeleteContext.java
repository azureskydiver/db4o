/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.internal.marshall.MarshallerFamily;

/**
 * @exclude
 */
public class DeleteContext {
	
	private final MarshallerFamily _family;

	private final StatefulBuffer _buffer;

	public DeleteContext(MarshallerFamily family, StatefulBuffer buffer){
		_family = family;
		_buffer = buffer;
	}

	public MarshallerFamily family() {
		return _family;
	}

	public StatefulBuffer buffer() {
		return _buffer;
	}
	
	

}
