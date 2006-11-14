/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.yetac.doctor.cmd;

public interface SourcePathResolver {
	SourcePathResolver IDENTITY=new SourcePathResolver() {

		public String resolve(String path) {
			return path;
		}
		
	};
	
	String resolve(String path);
}
