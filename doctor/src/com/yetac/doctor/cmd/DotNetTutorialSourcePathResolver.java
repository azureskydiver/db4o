/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.yetac.doctor.cmd;


public class DotNetTutorialSourcePathResolver implements SourcePathResolver {

	private final static String PACKAGEPATH="F1";
	public final static String DEFAULT_PACKAGE_PATH="com/db4o/f1";

	
	
	public String resolve(String path) {
        return path.replaceAll(DEFAULT_PACKAGE_PATH,PACKAGEPATH)
        		.replaceAll("chapter", "Chapter");
	}
	
}
