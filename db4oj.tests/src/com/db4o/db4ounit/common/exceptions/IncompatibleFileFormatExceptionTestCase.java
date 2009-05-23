/* Copyright (C) 2007 Versant Inc. http://www.db4o.com */
/**
 * @sharpen.if !SILVERLIGHT
 */
package com.db4o.db4ounit.common.exceptions;

import com.db4o.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.ext.*;
import com.db4o.foundation.io.*;
import com.db4o.io.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class IncompatibleFileFormatExceptionTestCase extends TestWithTempFile implements Db4oTestCase {
	
	public static void main(String[] args) throws Exception {
		new ConsoleTestRunner(IncompatibleFileFormatExceptionTestCase.class).run();
	}

	public void setUp() throws Exception {
		File4.delete(tempFile());
		IoAdapter adapter = new RandomAccessFileAdapter();
		adapter = adapter.open(tempFile(), false, 0, false);
		adapter.write(new byte[] { 1, 2, 3 }, 3);
		adapter.close();
	}

	public void test() {
		Assert.expect(IncompatibleFileFormatException.class, new CodeBlock() {
			public void run() throws Throwable {
				Db4o.openFile(tempFile());
			}
		});
		File4.delete(tempFile());
		IoAdapter adapter = new RandomAccessFileAdapter();
		Assert.isFalse(adapter.exists(tempFile()));
	}

}
