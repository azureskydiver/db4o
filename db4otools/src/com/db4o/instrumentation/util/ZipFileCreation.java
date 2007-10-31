/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.instrumentation.util;

import java.io.*;
import java.util.zip.*;

import com.db4o.foundation.io.*;

/**
 * Creates a zip from the contents of a directory.
 * 
 * The operation is performed as a side effect of the
 * constructor execution.
 */
public class ZipFileCreation {

	private final ZipOutputStream _zipFile;
	private final File _baseDir;

	public ZipFileCreation(String sourceDir, File outputFile) throws IOException {
		_baseDir = new File(sourceDir);
		_zipFile = new ZipOutputStream(new FileOutputStream(outputFile));
		try {
			writeEntries(_baseDir.listFiles());
		} finally {
			_zipFile.flush();
			_zipFile.close();
		}
	}

	private void writeEntries(File[] files) throws IOException {
		for (int i = 0; i < files.length; i++) {
			writeEntry(files[i]);
		}
	}

	private void writeEntry(File file) throws IOException {
		if (file.isDirectory()) {
			writeEntries(file.listFiles());
			return;
		}
		writeFileEntry(file);
	}

	private void writeFileEntry(File file) throws IOException {
		_zipFile.putNextEntry(entryForFile(file));
		try {
			_zipFile.write(readAllBytes(file));
		} finally {
			_zipFile.closeEntry();
		}
	}

	private byte[] readAllBytes(File file) throws IOException {
		return File4.readAllBytes(file.getAbsolutePath());
	}

	private ZipEntry entryForFile(File file) {
		return new ZipEntry(relativePath(file));
	}

	private String relativePath(File file) {
		return _baseDir.toURI().relativize(file.toURI()).getPath();
	}
}
