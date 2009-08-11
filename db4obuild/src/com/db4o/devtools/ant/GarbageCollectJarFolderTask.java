/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.devtools.ant;

import java.io.*;
import java.util.*;

import org.apache.tools.ant.*;

public class GarbageCollectJarFolderTask extends Task {

	private static final int MAX_FILES = 2;
	private File _jarFolder;
	
	public void setJarFolder(File jarFolder) {
		_jarFolder = jarFolder;
	}
	
	public void execute() throws BuildException {
		if(!_jarFolder.isDirectory()) {
			throw new BuildException("jarFolder - not an existing directory: " + _jarFolder);
		}
		File[] files = _jarFolder.listFiles();
		if(files.length >= MAX_FILES) {
			Arrays.sort(files, new Comparator<File>() {
				public int compare(File f1, File f2) {
					return new Long(f1.lastModified()).compareTo(f2.lastModified());
				}
			});
			for(int fileIdx = 0; fileIdx < files.length - MAX_FILES; fileIdx++) {
				System.err.println("DELETE " + files[fileIdx]);
				files[fileIdx].delete();
			}
		}
	}
	
}
