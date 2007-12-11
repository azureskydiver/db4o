/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.internal.marshall.*;

/**
 * @exclude
 */
public class DefragmentContextImpl implements DefragmentContext{
	
	private MarshallerFamily _mf;
	
	private BufferPair _readers;
	
	boolean _redirect;	
	
	public DefragmentContextImpl(MarshallerFamily mf, BufferPair readers, boolean redirect) {
		_mf= mf;
		_readers = readers;
		_redirect = redirect;
	}
	
	public MarshallerFamily marshallerFamily() {
		return _mf;
	}
	
	public BufferPair readers() {
		return _readers;
	}
	
	public boolean redirect() {
		return _redirect;
	}	

}
