/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.db4ounit.common.exceptions;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.io.*;
import com.db4o.io.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class IncompatibleFileFormatExceptionTestCase implements Db4oTestCase, TestLifeCycle {
	
	public static void main(String[] args) throws Exception {
		new ConsoleTestRunner(IncompatibleFileFormatExceptionTestCase.class).run();
	}

	private static final String INCOMPATIBLE_FILE_FORMAT = Path4.getTempFileName();

	public void setUp() throws Exception {
		File4.delete(INCOMPATIBLE_FILE_FORMAT);
		IoAdapter adapter = new RandomAccessFileAdapter();
		adapter = adapter.open(INCOMPATIBLE_FILE_FORMAT, false, 0, false);
		adapter.write(new byte[] { 1, 2, 3 }, 3);
		adapter.close();
	}

	public void tearDown() throws Exception {
		File4.delete(INCOMPATIBLE_FILE_FORMAT);
	}

	public void test() {
		Assert.expect(IncompatibleFileFormatException.class, new CodeBlock() {
			public void run() throws Throwable {
				Db4o.openFile(INCOMPATIBLE_FILE_FORMAT);
			}
		});
		File4.delete(INCOMPATIBLE_FILE_FORMAT);
		IoAdapter adapter = new RandomAccessFileAdapter();
		Assert.isFalse(adapter.exists(INCOMPATIBLE_FILE_FORMAT));
	}

}
