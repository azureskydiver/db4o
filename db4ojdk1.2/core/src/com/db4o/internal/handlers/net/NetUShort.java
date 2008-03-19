/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers.net;

import com.db4o.reflect.*;

/**
 * @exclude
 * @sharpen.ignore
 */
public class NetUShort extends NetSimpleTypeHandler{

	public NetUShort(Reflector reflector) {
		super(reflector, 24, 2);
	}
	
	public String toString(byte[] bytes) {
	    int val = 0;
		for (int i = 0; i < 2; i++){
			val = (val << 8) + (bytes[i] & 0xff);
		}
		return "" + val ; //$NON-NLS-1$
	}
}
