/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.instrumentation.main;

import java.io.*;
import java.net.*;
import java.util.*;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;

import com.db4o.foundation.io.*;
import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.file.*;

/**
 * @exclude
 */
public class Db4oFileEnhancer {
	private final BloatClassEdit _classEdit;
	
	public Db4oFileEnhancer(BloatClassEdit classEdit) {
		_classEdit = classEdit;
	}

	public void enhance(String sourceDir, String targetDir, String[] classpath, String packagePredicate) throws Exception {
		enhance(new DefaultFilePathRoot(new String[]{ sourceDir }, ".class"), targetDir, classpath, packagePredicate);
	}

	public void enhance(FilePathRoot sources, String targetDir, String[] classpath,
			String packagePredicate) throws Exception {
		enhance(new DefaultClassSource(), sources, targetDir, classpath, packagePredicate);
	}

	private void enhance(ClassSource classSource, FilePathRoot sources,String targetDir,String[] classpath,String packagePredicate) throws Exception {
		File fTargetDir = new File(targetDir);
		
		String[] sourceRoots = sources.rootDirs();
		for (int rootIdx = 0; rootIdx < sourceRoots.length; rootIdx++) {
			File rootFile = new File(sourceRoots[rootIdx]);
			assertSourceDir(rootFile);
		}
		
		ClassFileLoader fileLoader=new ClassFileLoader(classSource);
		String[] fullClasspath = fullClasspath(sources, classpath);
		setOutputDir(fileLoader, fTargetDir);
		setClasspath(fileLoader, fullClasspath);
		
		URL[] urls = classpathToURLs(fullClasspath);	
		URLClassLoader classLoader=new URLClassLoader(urls,ClassLoader.getSystemClassLoader());
		enhance(sources,fTargetDir,classLoader,new BloatLoaderContext(fileLoader),packagePredicate);
		
		fileLoader.done();
	}

	private void enhance(
			FilePathRoot sources,
			File target,
			ClassLoader classLoader,
			BloatLoaderContext bloatUtil,
			String packagePredicate) throws Exception {
		for (Iterator sourceFileIter = sources.files(); sourceFileIter.hasNext();) {
			FileWithRoot file = (FileWithRoot) sourceFileIter.next();
			enhanceFile(file.root(), file.file(), target, classLoader, bloatUtil, packagePredicate);
		}
	}

	private void enhanceFile(
			File prefix, 
			File source, 
			File target,
			ClassLoader classLoader, 
			BloatLoaderContext bloatUtil,
			String packagePredicate) throws IOException {
		String classPath = source.getCanonicalPath().substring(prefix.getCanonicalPath().length()+1);
		String className = classPath.substring(0, classPath.length()-".class".length());
		className=className.replace(File.separatorChar,'.');
		
		InstrumentationStatus status = InstrumentationStatus.NOT_INSTRUMENTED;
		try {
			if (className.startsWith(packagePredicate)) {
				System.err.println("Processing " + className);
				ClassEditor classEditor = bloatUtil.classEditor(className);
				status = _classEdit.enhance(classEditor, classLoader, bloatUtil);
				System.err.println("enhance " + className + ": " + (status.isInstrumented() ? "ok" : "failed"));
			}
		} catch (Exception e) {
			status = InstrumentationStatus.FAILED;
			e.printStackTrace();
		} catch (NoClassDefFoundError e) {
			System.err.println("Omitting " + className + ": Referenced class " + e.getMessage() + " not found.");
		} finally {
			if (!status.isInstrumented()) {
				File targetFile = new File(target, classPath);
				targetFile.getParentFile().mkdirs();
				File4.copyFile(source, targetFile);
			}
			else {
//				bloatUtil.commit();
			}
		}
	}

	private String[] fullClasspath(FilePathRoot sources, String[] classpath) {
		String[] sourceRoots = sources.rootDirs();
		String [] fullClasspath = new String[sourceRoots.length + classpath.length];
		System.arraycopy(sourceRoots, 0, fullClasspath, 0, sourceRoots.length);
		System.arraycopy(classpath, 0, fullClasspath, sourceRoots.length, classpath.length);
		return fullClasspath;
	}

	private void setOutputDir(ClassFileLoader fileLoader, File fTargetDir) {
		fileLoader.setOutputDir(fTargetDir);
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
	
}
