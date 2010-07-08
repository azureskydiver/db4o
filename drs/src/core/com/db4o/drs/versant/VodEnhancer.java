/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import java.io.*;

import com.versant.core.jdo.tools.enhancer.*;

public class VodEnhancer {
	
	public static void main(String[] args) throws Exception {
		
		if(args == null || args.length != 1){
			throw new RuntimeException("Expected one argument: PropertiesFilePath");
		}
		String propertiesFilePath = args[0];
		
		
		
		Enhancer enhancer = new Enhancer();
		enhancer.setPropertiesFile(new File(propertiesFilePath));
		enhancer.enhance();
	}

}
