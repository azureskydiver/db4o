/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import java.io.*;

import com.versant.core.jdo.tools.enhancer.*;

public class VodEnhancer {
	
	public static void main(String[] args) throws Exception {
		
		if(args == null || args.length != 2){
			throw new RuntimeException("Expected two args: PropertiesFilePath and outputDir");
		}
		
		String propertiesFilePath = args[0];
		String outputDir = args[1];
		
		
		Enhancer enhancer = new Enhancer();
		enhancer.setPropertiesFile(new File(propertiesFilePath));
		
		System.out.println("OTUOSJSJS");
		System.out.println(new File(outputDir).getAbsolutePath());
		
		
		enhancer.setOutputDir(new File(outputDir));
		enhancer.enhance();
	}

}
