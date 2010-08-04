/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import java.io.*;

public class JdoMetadataGenerator {
	
	private final File _root;

	public JdoMetadataGenerator(File root) {
		_root = root;
	}
	
	public File generate(String packageName) {
		try {
			String path = packageName.replace('.', '/');
			File packageFolder = new File(_root, path);
			FileWriter fileWriter;
			File jdoFile = new File(packageFolder, "package.jdo");
			fileWriter = new FileWriter(jdoFile);
			PrintWriter writer = new PrintWriter(fileWriter);
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.println("<!DOCTYPE jdo PUBLIC '-//Sun Microsystems, Inc.//DTD Java Data Objects Metadata 2.2//EN' 'http://java.sun.com/dtd/jdo_2_2.dtd'>");
			writer.println("<jdo>");
			writer.println("    <package name=\"" + packageName + "\">");
			for (File file : packageFolder.listFiles()) {
				if(! file.isDirectory()  && file.getName().endsWith(".class")){
					writer.print("    	<class name=\"");
					writer.print(className(_root, file));
					writer.println("\"/>");
				}
			}
			writer.println("    </package>");
			writer.println("</jdo>");
			writer.flush();
			writer.close();
			return jdoFile;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String className(File root, File file) {
		int rootPathLength = root.getAbsolutePath().length();
		String name = file.getAbsolutePath().substring(rootPathLength + 1);
		return stripDotClass(name.replace(File.separatorChar, '.'));
	}

	private String stripDotClass(String name) {
		return name.substring(0, name.length() - 6);
	}

}
