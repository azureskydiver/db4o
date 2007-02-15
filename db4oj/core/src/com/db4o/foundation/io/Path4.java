/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation.io;


/**
 * IMPORTANT: Keep the interface of this class compatible with .NET System.IO.Path otherwise
 * bad things will happen to you.
 * 
 * @sharpen.ignore
 */
public class Path4 {
	public static String combine(String parent, String child) {		
		return parent.endsWith(java.io.File.separator)
        ? parent + child
        : parent + java.io.File.separator + child;
	}
	
	public static String getTempPath() {
		String path = System.getProperty("java.io.tmpdir"); 
		if(path != null && path.length() > 0){
			return path;
		}
		return "/temp";
	}
}
