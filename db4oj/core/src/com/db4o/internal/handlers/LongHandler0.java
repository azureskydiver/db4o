/* Copyright (C) 2004 - 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.internal.*;
import com.db4o.marshall.*;

public class LongHandler0 extends LongHandler {

	public LongHandler0(ObjectContainerBase stream) {
		super(stream);
	}
	
	public Object read(ReadContext context) {
		Long value = (Long)super.read(context);
		if (value.longValue() == Long.MAX_VALUE) {
			return null;
		}
		return value;
	}
	
}
