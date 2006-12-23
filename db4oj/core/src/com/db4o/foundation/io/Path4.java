/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation.io;

import java.io.File;

/**
 * @sharpen.ignore
 */
public class Path4 {
	public static String combine(String parent, String child) {		
		return parent.endsWith(java.io.File.separator)
        ? parent + child
        : parent + java.io.File.separator + child;
	}
	
	public static String getTempPath() {
		return System.getProperty("java.io.tmpdir");
	}
	
	public static String buildTempPath(String fname) {
		return new File(getTempPath(), fname).getAbsolutePath();
	}
}
