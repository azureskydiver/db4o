/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.main;

import java.io.*;
import java.net.*;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;

import com.db4o.nativequery.bloat.*;
import com.db4o.nativequery.optimization.*;
import com.db4o.query.*;

public class Db4oFileEnhancer {
	private NativeQueryEnhancer enhancer=new NativeQueryEnhancer();
	
	public void enhance(String sourceDir,String targetDir,String[] classPath,String packagePredicate) throws Exception {
		File source = new File(sourceDir);
		File target = new File(targetDir);
		
		ClassFileLoader fileLoader=new ClassFileLoader();
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
		enhance(source.getCanonicalPath(),source,target,classLoader,new BloatUtil(fileLoader),packagePredicate);
		fileLoader.done();
	}
	
	private void enhance(String prefix,File source,File target,ClassLoader classLoader,BloatUtil bloatUtil,String packagePredicate) throws Exception {
		if(source.isDirectory()) {
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
		else {
			String className = source.getCanonicalPath().substring(prefix.length()+1);
			className = className.substring(0, className.length()-".class".length());
			className=className.replace(File.separatorChar,'.');
			if(!className.startsWith(packagePredicate)) {
				copyFile(source,target);
				return;
			}
			try {
				Class clazz = classLoader.loadClass(className);
				Class filterClass = classLoader.loadClass(Predicate.class.getName());
				if(filterClass.isAssignableFrom(clazz)&&filterClass!=clazz) {
					System.err.println("Processing "+className);
					ClassEditor classEditor=bloatUtil.classEditor(className);
					enhancer.enhance(bloatUtil,classEditor,Predicate.PREDICATEMETHOD_NAME,classLoader);
				}
				else {
					copyFile(source,target);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} catch (NoClassDefFoundError e) {
				System.err.println("Omitting "+className+": Referenced class "+e.getMessage()+" not found.");
				copyFile(source,target);
			}
		}
	}
	
	private void copyFile(File source, File target) throws IOException {
		BufferedInputStream in=new BufferedInputStream(new FileInputStream(source));
		BufferedOutputStream out=new BufferedOutputStream(new FileOutputStream(target));
		try {
			byte[] buffer=new byte[4096];
			int bytesRead=0;
			while((bytesRead=in.read(buffer))>=0) {
				out.write(buffer,0,bytesRead);
			}
			out.flush();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
			}
			try {
				out.close();
			} catch (IOException e) {
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new Db4oFileEnhancer().enhance(args[0],args[1],new String[]{},"");
	}
}
