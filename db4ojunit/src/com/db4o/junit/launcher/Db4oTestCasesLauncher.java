package com.db4o.junit.launcher;

import java.io.*;
import java.util.*;

import junit.framework.TestCase;

import com.db4o.db4ounit.common.assorted.LongLinkedListTestCase;
import com.db4o.db4ounit.common.diagnostics.MissingClassDiagnosticsTestCase;
import com.db4o.db4ounit.common.ta.mixed.*;
import com.db4o.db4ounit.common.ta.nonta.NonTALinkedListTestCase;
import com.db4o.foundation.io.Path4;

import db4ounit.extensions.Db4oTestSuiteBuilder;

public class Db4oTestCasesLauncher extends TestCase {

	private static final String IGNORE_LIST_FILE = "/ignore.txt";

	private static final String SDCARD_PATH = "Dalvik".equals(System.getProperty("java.vm.name")) ? "/sdcard" : Path4.getTempPath();
	private static final String ACCEPTED_CLASSES_FILE = SDCARD_PATH + "/accepted-classes.txt";
	private static final String TESTS_OUTPUT_FILE = SDCARD_PATH + "/db4o-tests-output.txt";

	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException {
		new Db4oTestCasesLauncher().test(new PrintWriter(System.out));
	}

	protected PrintWriter acceptedClasses = null;

	public void test() throws FileNotFoundException, ClassNotFoundException {
		PrintWriter out = new PrintWriter(TESTS_OUTPUT_FILE);
		test(out);
	}

	private void test(final PrintWriter out) {

		System.setProperty("db4ounit.file.path", SDCARD_PATH);

		final Collection<String> ignoreList = ignoreList();

		try {
			acceptedClasses = new PrintWriter(ACCEPTED_CLASSES_FILE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			Class<?>[] classes = new Class[] { 
//					com.db4o.db4ounit.common.AllTests.class, 
//					BlockAwareBinTestSuite.class
//					all
					
					com.db4o.db4ounit.jre11.AllTests.class,
					com.db4o.db4ounit.jre12.AllTestsJdk1_2.class,		
					
					// stackoverflows:
//					MissingClassDiagnosticsTestCase.class,
//					LongLinkedListTestCase.class,
//					MixedArrayTestCase.class,
//					NonTALinkedListTestCase.class,
//					NTNTestCase.class
					
					};

			SelectiveDb4oAndroidFixture fixture = new SelectiveDb4oAndroidFixture(acceptedClasses, out, ignoreList);

			Db4oTestSuiteBuilder suite = new Db4oTestSuiteBuilder(fixture, classes);

			int retcode = new VerboseConsoleTestRunner(suite, out).run(out);
			if (retcode > 0) {
				fail();
			}
		} finally {
			out.flush();
			out.close();
		}
	}

	private Collection<String> ignoreList() {

		final Collection<String> ignoreList = new HashSet<String>();

		InputStream ignoreListStream = getClass().getResourceAsStream(IGNORE_LIST_FILE);

		if (ignoreListStream == null) {
			return ignoreList;
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(ignoreListStream));
		try {

			String line;
			while ((line = in.readLine()) != null) {
				int t = line.lastIndexOf('.');
				if (t != -1 && line.substring(t).startsWith(".test")) {
					line = line.substring(0, t);
				}
				if (line.lastIndexOf(' ') != -1) {
					line = line.substring(line.lastIndexOf(' ') + 1);
				}
				if (line.lastIndexOf('$') != -1) {
					ignoreList.add(line);
					line = line.substring(0, line.lastIndexOf('$'));
				}
				ignoreList.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ignoreList;
	}
}
