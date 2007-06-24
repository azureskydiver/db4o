/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.foundation.io.*;

public final class SwitchingFilesFromClientUtil {

	static final String FILENAME_A = "switchto.a.db4o";
	static final String FILENAME_B = "switchto.b.db4o";
	public static final String MAINFILE_NAME = "mainfile";

	private SwitchingFilesFromClientUtil() {
	}

	static void deleteFiles() {
		File4.delete(MAINFILE_NAME);
		File4.delete(FILENAME_A);
		File4.delete(FILENAME_B);
	}
	
}
