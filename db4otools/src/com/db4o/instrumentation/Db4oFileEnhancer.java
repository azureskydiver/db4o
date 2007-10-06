/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.instrumentation;

import java.io.*;
import java.net.*;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;

import com.db4o.foundation.io.*;

public class Db4oFileEnhancer {
	private final BloatClassEdit _classEdit;
	
	public Db4oFileEnhancer(BloatClassEdit classEdit) {
		_classEdit = classEdit;
	}

	/**
	 * 
	 * @param sourceDir
	 * @param targetDir
	 * @param classPath
	 *            includes the sourceDir
	 * @param packagePredicate
	 * @throws Exception
	 */
	public void enhance(String sourceDir, String targetDir, String[] classpath,
			String packagePredicate) throws Exception {
		enhance(new DefaultClassSource(), sourceDir, targetDir, classpath,
				packagePredicate);
	}

	private void enhance(ClassSource classSource, String sourceDir,String targetDir,String[] classpath,String packagePredicate) throws Exception {
		File fSourceDir = new File(sourceDir);
		File fTargetDir = new File(targetDir);
		
		assertSourceDir(fSourceDir);
		
		ClassFileLoader fileLoader=new ClassFileLoader(classSource);
		fileLoader.setOutputDir(fTargetDir);
		setClasspath(fileLoader, classpath);	
		URL[] urls = classpathToURLs(classpath);
		
		URLClassLoader classLoader=new URLClassLoader(urls,ClassLoader.getSystemClassLoader());
		enhance(fSourceDir.getCanonicalPath(),fSourceDir,fTargetDir,classLoader,new BloatLoaderContext(fileLoader),packagePredicate);
		
		fileLoader.done();
	}

	private void assertSourceDir(File fSourceDir) throws IOException {
		if(!fSourceDir.isDirectory()) {
			throw new IOException("No directory: "+fSourceDir.getCanonicalPath());
		}
	}

	private void setClasspath(ClassFileLoader fileLoader, String[] classPath) {
		for (int pathIdx = 0; pathIdx < classPath.length; pathIdx++) {
			fileLoader.appendClassPath(classPath[pathIdx]);
		}
	}
	
	private URL[] classpathToURLs(String[] classPath) throws MalformedURLException {
		URL[] urls=new URL[classPath.length];
		for (int pathIdx = 0; pathIdx < classPath.length; pathIdx++) {
			urls[pathIdx]=new File(classPath[pathIdx]).toURL();
		}
		return urls;
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
		
		boolean enhanced = false;
		try {
			if (className.startsWith(packagePredicate)) {
				System.err.println("Processing " + className);
				ClassEditor classEditor = bloatUtil.classEditor(className);
				enhanced = _classEdit.bloat(classEditor, classLoader, bloatUtil);
				System.err.println("enhance " + className + (enhanced ? "ok" : "failed"));
			}
		} catch (Exception e) {
			enhanced = true;
			e.printStackTrace();
		} catch (NoClassDefFoundError e) {
			System.err.println("Omitting " + className + ": Referenced class " + e.getMessage() + " not found.");
		} finally {
			if (!enhanced) {
				File4.copyFile(source, target);
			}
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
