/* Copyright (C) 2004 - 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.internal.*;
import com.db4o.marshall.*;

public class DoubleHandler0 extends DoubleHandler {

	public DoubleHandler0(ObjectContainerBase stream) {
		super(stream);
	}
	
	public Object read(ReadContext context) {
		long value = context.readLong();
		if (value == Long.MAX_VALUE) {
			return null;
		}
		return new Double(Platform4.longToDouble(value));
	}

}
