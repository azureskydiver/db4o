/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.test.util;

import java.io.*;

import com.db4o.foundation.io.*;

public class IOUtil {
	/**
	 * Deletes all files under this directory. It doesn't delete the directory
	 * itself and all files in the sub-directory.
	 * 
	 * @param dir
	 * @throws IOException
	 */
	public static void deleteDir(String dir) throws IOException {
		File source = new File(dir);
		String sourceAbsolutePath = source.getCanonicalPath();
		String[] files = source.list();
		for (int i = 0; i < files.length; i++) {
			File4.delete(Path4.combine(sourceAbsolutePath, files[i]));
		}
	}
}
