/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.handlers;

import com.db4o.*;
import com.db4o.inside.*;

/**
 * @exclude
 */
public class NetUShort extends NetSimpleTypeHandler{

	public NetUShort(ObjectContainerBase stream) {
		super(stream, 24, 2);
	}
	
	public String toString(byte[] bytes) {
	    int val = 0;
		for (int i = 0; i < 2; i++){
			val = (val << 8) + (bytes[i] & 0xff);
		}
		return "" + val ;
	}
}
