/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers.net;

import com.db4o.internal.*;

/**
 * @exclude
 * @sharpen.ignore
 */
public class NetSByte extends NetSimpleTypeHandler{

	public NetSByte(ObjectContainerBase stream) {
		super(stream, 20, 1);
	}
	
	public String toString(byte[] bytes) {
		byte b = bytes[0];
		b -= 128; 
		return "" + b;
	}
}
