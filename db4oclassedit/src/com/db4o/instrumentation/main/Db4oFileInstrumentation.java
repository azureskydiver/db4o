/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.instrumentation.main;

import java.io.*;
import java.net.*;
import java.util.*;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;

import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.file.*;

/**
 * @exclude
 */
public class Db4oFileInstrumentation {
	private final BloatClassEdit _classEdit;
	
	public Db4oFileInstrumentation(BloatClassEdit classEdit) {
		_classEdit = classEdit;
	}
	
	public Db4oFileInstrumentation(BloatClassEdit[] classEdits) {
		this(new CompositeBloatClassEdit(classEdits));
	}

	// TODO: Remove packagePrefix, we now use class filters 
	public void enhance(String sourceDir, String targetDir, String[] classpath, String packagePrefix) throws Exception {
		enhance(new DefaultFilePathRoot(new String[]{ sourceDir }, ".class"), targetDir, classpath, packagePrefix);
	}

	public void enhance(FilePathRoot sources, String targetDir, String[] classpath,
			String packagePrefix) throws Exception {
		enhance(new DefaultClassSource(), sources, targetDir, classpath, packagePrefix);
	}

	private void enhance(ClassSource classSource, FilePathRoot sources,String targetDir,String[] classpath,String packagePrefix) throws Exception {
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
		enhance(sources,fTargetDir,classLoader,new BloatLoaderContext(fileLoader),packagePrefix);
		
		fileLoader.done();
	}

	private void enhance(
			FilePathRoot sources,
			File target,
			ClassLoader classLoader,
			BloatLoaderContext bloatUtil,
			String packagePrefix) throws Exception {
		for (Iterator sourceFileIter = sources.files(); sourceFileIter.hasNext();) {
			InstrumentationClassSource file = (InstrumentationClassSource) sourceFileIter.next();
			enhanceFile(file, target, classLoader, bloatUtil, packagePrefix);
		}
	}

	private void enhanceFile(
			InstrumentationClassSource source, 
			File target,
			ClassLoader classLoader, 
			BloatLoaderContext bloatUtil,
			String packagePrefix) throws IOException, ClassNotFoundException {
		
		if (!source.className().startsWith(packagePrefix)) {
			return;
		}
		System.err.println("Processing " + source.className());
		ClassEditor classEditor = bloatUtil.classEditor(source.className());
		InstrumentationStatus status = _classEdit.enhance(classEditor, classLoader, bloatUtil);
		System.err.println("enhance " + source.className() + ": " + (status.isInstrumented() ? "ok" : "skipped"));
		if (!status.isInstrumented()) {
			File targetFile = source.targetPath(target);
			targetFile.getParentFile().mkdirs();
			copy(source.inputStream(), targetFile);
		}
	}

	private void copy(InputStream inputStream, File targetFile) throws IOException {
		final int bufSize = 4096;
		BufferedInputStream bufIn = new BufferedInputStream(inputStream, bufSize);
		try {
			BufferedOutputStream bufOut = new BufferedOutputStream(new FileOutputStream(targetFile));
			try {
				copy(bufSize, bufIn, bufOut);
			}
			finally {
				bufOut.close();
			}
		}
		finally {
			bufIn.close();
		}
	}

	private void copy(final int bufSize, BufferedInputStream bufIn,
			BufferedOutputStream bufOut) throws IOException {
		byte[] buf = new byte[bufSize];
		int bytesRead = -1;
		while((bytesRead = bufIn.read(buf)) >= 0) {
			bufOut.write(buf, 0, bytesRead);
		}
	}

	private String[] fullClasspath(FilePathRoot sources, String[] classpath) throws IOException {
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
			//throw new IOException("No directory: "+fSourceDir.getCanonicalPath());
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
			urls[pathIdx]=toURL(classPath[pathIdx]);
		}
		return urls;
	}

	/**
	 * @deprecated
	 * 
	 * @throws MalformedURLException
	 */
	private URL toURL(final String classPathItem) throws MalformedURLException {
		return new File(classPathItem).toURL();
	}
}
