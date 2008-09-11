/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;

import java.io.*;

/**
 * @sharpen.ignore
 */
public class UTF8Encoder {
	
	private final static String CHARSET_NAME = "UTF-8";
	
	public byte[] encode(String str) throws UnsupportedEncodingException{
		return str.getBytes(CHARSET_NAME);
	}
	
	public String decode(byte[] bytes, int start, int length) throws UnsupportedEncodingException {
		return new String(bytes, start, length, CHARSET_NAME);
	}

}
