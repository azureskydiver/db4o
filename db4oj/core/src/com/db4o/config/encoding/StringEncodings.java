/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.config.encoding;

/**
 * @exclude
 */
public class StringEncodings {
	
	public static StringEncoding utf8() {
		return new UTF8StringEncoding();
	}
	
	public static StringEncoding unicode() {
		return new UnicodeStringEncoding();
	}
	
	public static StringEncoding latin() {
		return new LatinStringEncoding();
	}

}
