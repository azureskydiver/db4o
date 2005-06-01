/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.handlers;

import com.db4o.*;

/**
 * @exclude
 */
public class NetDecimal extends NetSimpleTypeHandler{

	public NetDecimal(YapStream stream) {
		super(stream, 21, 16);
	}
	
	public String toString(byte[] bytes) {
		return "no converter for System.Decimal, mscorlib";
	}
}
