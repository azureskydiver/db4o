/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.handlers;

import com.db4o.*;

/**
 * @exclude
 */
public class NetULong extends NetSimpleTypeHandler{

	public NetULong(YapStream stream) {
		super(stream, 23, 8);
	}
	
	public String toString(byte[] bytes) {
	    long  l = 0;
		for (int i = 0; i < 8; i++){
			l = (l << 8) + (bytes[i] & 0xff);
		}
		if(l >= 0) {
			return "" + l ;
		}
		return ".NET System.UInt64 overflow";
	}
	
}
