/* Copyright (C) 2004 - 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.internal.*;
import com.db4o.marshall.*;

public class FloatHandler0 extends FloatHandler {

	public FloatHandler0(ObjectContainerBase stream) {
		super(stream);
	}
	
	public Object read(ReadContext context) {
		Float value = (Float)super.read(context);
		if (value.isNaN()) {
			return null;
		}
		return value;
	}

}
