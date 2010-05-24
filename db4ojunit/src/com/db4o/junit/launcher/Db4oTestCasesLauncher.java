package com.db4o.junit.launcher;

import java.io.*;

import junit.framework.TestCase;
import db4ounit.ConsoleTestRunner;

public class Db4oTestCasesLauncher extends TestCase {

	public void test() throws FileNotFoundException {
		PrintWriter out = new PrintWriter("/sdcard/db4o-tests-output.txt");
		try {
			int retcode = new ConsoleTestRunner(com.db4o.db4ounit.common.foundation.AllTests.class).run(out);
			if (retcode > 0) {
				fail();
			}
		} finally {
			out.flush();
			out.close();
		}
	}

}
