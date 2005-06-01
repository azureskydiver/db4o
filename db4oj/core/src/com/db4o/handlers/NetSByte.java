/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.handlers;

import com.db4o.*;

/**
 * @exclude
 */
public class NetSByte extends NetSimpleTypeHandler{

	public NetSByte(YapStream stream) {
		super(stream, 20, 1);
	}
	
	public String toString(byte[] bytes) {
		byte b = bytes[0];
		b -= 128; 
		return "" + b;
	}
}
