/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.handlers;

import com.db4o.*;
import com.db4o.inside.*;

/**
 * @exclude
 */
public class NetUInt extends NetSimpleTypeHandler{

	public NetUInt(YapStream stream) {
		super(stream, 22, 4);
	}
	
	public String toString(byte[] bytes) {
	    long  l = 0;
		for (int i = 0; i < 4; i++){
			l = (l << 8) + (bytes[i] & 0xff);
		}
		return "" + l ;
	}
}
