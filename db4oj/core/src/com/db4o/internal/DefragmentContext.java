/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.internal.marshall.MarshallerFamily;

public interface DefragmentContext {
	
	public MarshallerFamily marshallerFamily();
	
	public BufferPair readers();
	
	public boolean redirect();
}
