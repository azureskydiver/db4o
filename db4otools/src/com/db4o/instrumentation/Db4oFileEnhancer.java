/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.instrumentation;

import java.io.*;
import java.net.*;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;

import com.db4o.foundation.io.*;
import com.db4o.instrumentation.*;

public class Db4oFileEnhancer {
	private final BloatClassEdit classEdit;
	
	public Db4oFileEnhancer(BloatClassEdit classEdit) {
		this.classEdit = classEdit;
	}

	public void enhance(String sourceDir,String targetDir,String[] classPath,String packagePredicate) throws Exception {
		enhance(new DefaultClassSource(), sourceDir, targetDir, classPath, packagePredicate);
	}

	public void enhance(ClassSource classSource, String sourceDir,String targetDir,String[] classPath,String packagePredicate) throws Exception {
		File source = new File(sourceDir);
		File target = new File(targetDir);
		
		ClassFileLoader fileLoader=new ClassFileLoader(classSource);
		fileLoader.setClassPath(sourceDir);
		URL[] urls=new URL[classPath.length+1];
		urls[0]=source.toURL();
		for (int pathIdx = 0; pathIdx < classPath.length; pathIdx++) {
			fileLoader.appendClassPath(classPath[pathIdx]);
			urls[pathIdx+1]=new File(classPath[pathIdx]).toURL();
		}
		URLClassLoader classLoader=new URLClassLoader(urls,ClassLoader.getSystemClassLoader());
		fileLoader.setOutputDir(target);
		if(!source.isDirectory()) {
			throw new IOException("No directory: "+sourceDir);
		}
		enhance(source.getCanonicalPath(),source,target,classLoader,new BloatLoaderContext(fileLoader),packagePredicate);
		fileLoader.done();
	}
	
	private void enhance(
			String prefix,File source,
			File target,
			ClassLoader classLoader,
			BloatLoaderContext bloatUtil,
			String packagePredicate) throws Exception {
		if(source.isDirectory()) {
			enhanceDir(prefix, source, target, classLoader, bloatUtil, packagePredicate);
		}
		else {
			enhanceFile(prefix, source, target, classLoader, bloatUtil, packagePredicate);
		}
	}

	private void enhanceFile(
			String prefix, 
			File source, 
			File target,
			ClassLoader classLoader, 
			BloatLoaderContext bloatUtil,
			String packagePredicate) throws IOException {
		String className = source.getCanonicalPath().substring(prefix.length()+1);
		className = className.substring(0, className.length()-".class".length());
		className=className.replace(File.separatorChar,'.');
		if(!className.startsWith(packagePredicate)) {
			File4.copyFile(source,target);
			return;
		}
		try {
			System.err.println("Processing " + className);
			ClassEditor classEditor = bloatUtil.classEditor(className);
			boolean success = classEdit.bloat(classEditor, classLoader, bloatUtil);
			if (!success) {
				System.err.println("Could not enhance: " + className);
				File4.copyFile(source, target);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
		catch (NoClassDefFoundError e) {
			System.err.println("Omitting " + className + ": Referenced class " + e.getMessage() + " not found.");
			File4.copyFile(source, target);
		}
	}

	private void enhanceDir(
			String prefix, 
			File source, 
			File target,
			ClassLoader classLoader, 
			BloatLoaderContext bloatUtil,
			String packagePredicate) throws Exception {
		File[] subFiles=source.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory()||file.getName().endsWith(".class");
			}
		});
		target.mkdirs();
		for (int idx = 0; idx < subFiles.length; idx++) {
			enhance(prefix,subFiles[idx],new File(target,subFiles[idx].getName()),classLoader,bloatUtil,packagePredicate);
		}
	}
}
