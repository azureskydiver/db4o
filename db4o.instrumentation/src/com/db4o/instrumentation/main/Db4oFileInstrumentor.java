/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.instrumentation.main;

import java.io.*;
import java.net.*;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;

import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.file.*;

/**
 * @exclude
 */
public class Db4oFileInstrumentor {
	private final BloatClassEdit _classEdit;
	
	public Db4oFileInstrumentor(BloatClassEdit classEdit) {
		_classEdit = classEdit;
	}
	
	public Db4oFileInstrumentor(BloatClassEdit[] classEdits) {
		this(new CompositeBloatClassEdit(classEdits));
	}

	public void enhance(String sourceDir, String targetDir, String[] classpath) throws Exception {
		enhance(new DefaultFilePathRoot(new String[]{ sourceDir }, ".class"), targetDir, classpath);
	}

	public void enhance(FilePathRoot sources, String targetDir, String[] classpath) throws Exception {
		enhance(new DefaultClassSource(), sources, targetDir, classpath);
	}

	public void enhance(ClassSource classSource, FilePathRoot sources,String targetDir,String[] classpath) throws Exception {
		enhance(classSource, sources, targetDir, classpath, ClassLoader.getSystemClassLoader());
	}

	public void enhance(ClassSource classSource, FilePathRoot sources,String targetDir,String[] classpath, ClassLoader parentClassLoader) throws Exception {
		File fTargetDir = new File(targetDir);
		
		ClassFileLoader fileLoader=new ClassFileLoader(classSource);
		String[] fullClasspath = fullClasspath(sources, classpath);
		setOutputDir(fileLoader, fTargetDir);
		setClasspath(fileLoader, fullClasspath);
		
		URL[] urls = classpathToURLs(fullClasspath);	
		URLClassLoader classLoader=new URLClassLoader(urls,parentClassLoader);
		enhance(sources,fTargetDir,classLoader,new BloatLoaderContext(fileLoader));
		
		fileLoader.done();
	}

	private void enhance(
			FilePathRoot sources,
			File target,
			ClassLoader classLoader,
			BloatLoaderContext bloatUtil) throws Exception {
		for (InstrumentationClassSource file : sources) {
			enhanceFile(file, target, classLoader, bloatUtil);
		}
	}

	private void enhanceFile(
			InstrumentationClassSource source, 
			File target,
			ClassLoader classLoader, 
			BloatLoaderContext bloatUtil) throws IOException, ClassNotFoundException {
		System.err.println("Processing " + source.className());
		ClassEditor classEditor = bloatUtil.classEditor(source.className());
		InstrumentationStatus status = _classEdit.enhance(classEditor, classLoader, bloatUtil);
		System.err.println("enhance " + source.className() + ": " + (status.isInstrumented() ? "ok" : "skipped"));
		if (!status.isInstrumented()) {
			File targetFile = source.targetPath(target);
			targetFile.getParentFile().mkdirs();
			copy(source, targetFile);
		}
	}

	private void copy(InstrumentationClassSource source, File targetFile) throws IOException {
	    
	    if(targetFile.equals(source.sourceFile())){
	        return;
	    }
	    
		final int bufSize = 4096;
		BufferedInputStream bufIn = new BufferedInputStream(source.inputStream(), bufSize);
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
