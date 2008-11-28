/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.foundation.*;

/**
 * @exclude
 */
public class InCallbackState {
	
	public final static DynamicVariable<Boolean> _inCallback = new DynamicVariable<Boolean>(){
		@Override
		protected Boolean defaultValue() {
			return false;
		}
	};


}
