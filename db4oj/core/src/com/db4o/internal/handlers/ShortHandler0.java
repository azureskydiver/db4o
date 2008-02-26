/* Copyright (C) 2004 - 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.marshall.*;

public class ShortHandler0 extends ShortHandler {

	public Object read(ReadContext context) {
		Short value = (Short)super.read(context);
		if (value.shortValue() == Short.MAX_VALUE) {
			return null;
		}
		return value;
		
	}

}
