/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.ta.instrumentation.test;

import java.io.*;
import java.util.zip.*;

/**
 * Aids in writing jar files (zip files that contains java classes and resources).
 */
public class JarFileWriter {

	private final ZipOutputStream _zipWriter;

	public JarFileWriter(File file) throws IOException {
		_zipWriter = new ZipOutputStream(new FileOutputStream(file));
	}

	public void writeClass(Class klass) throws IOException {
		beginEntry(ClassFiles.classNameAsPath(klass));
		try {
			_zipWriter.write(ClassFiles.classBytes(klass));
		} finally {
			endEntry();
		}
	}
	
	public void writeResourceString(String fileName, String contents) throws IOException {
		beginEntry(fileName);
		try {
			_zipWriter.write(contents.getBytes());
		} finally {
			endEntry();
		}
	}
	
	public void close() throws IOException {
		_zipWriter.close();
	}
	
	private void beginEntry(final String entryName) throws IOException {
		_zipWriter.putNextEntry(new ZipEntry(entryName));
	}

	private void endEntry() throws IOException {
		_zipWriter.flush();
		_zipWriter.closeEntry();
	}
}
