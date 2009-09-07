/* Copyright (C) 2009  Versant Inc.  http://www.db4o.com */

package com.yetac.doctor.runner.tests;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;

import junit.framework.*;

import com.yetac.doctor.runner.*;

public class ExampleRunnerTestCase extends TestCase {

	static final IllegalStateException PARAMETERLESS_EXCEPTION = new IllegalStateException();
	
//	static final IllegalStateException OBJECTCONTAINER_EXCEPTION = new IllegalStateException();
	
	public void testParameterlessExample() throws Exception {
		assertExample(PARAMETERLESS_EXCEPTION, "parameterless");
	}
	
//	public void testObjectContainerExample() throws Exception {
//		assertExample(OBJECTCONTAINER_EXCEPTION, "withObjectContainer");
//	}

	private void assertExample(IllegalStateException expectedException, String exampleName) throws Exception, MalformedURLException, IOException {
	    final File db4oArchive = db4oArchive("7.10");
		assertTrue(db4oArchive.exists());
		
		final ExampleRunner runner = new ExampleRunner(new URLClassLoader(new URL[] { db4oArchive.toURL() }, getClass().getClassLoader()), tempFile());
		try {
			runner.runExample(Example.class.getName(), exampleName, new ByteArrayOutputStream());
			Assert.fail("Expecting InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame(expectedException, e.getCause());
		} finally {
			runner.reset();
		}
    }

	public static class Example {
		public static void parameterless() {
			throw PARAMETERLESS_EXCEPTION;
		}
	}
	
	private File db4oArchive(String version) {
		return new File(findParentDirectory("db4o.archives"), "java1.2/db4o-" + version + "-java1.2.jar");
    }

	private File findParentDirectory(String directory) {
		File parent = classFile().getParentFile();
		while (parent != null) {
			final File candidate = new File(parent, directory);
			if (candidate.exists()) {
				return candidate;
			}
			parent = parent.getParentFile();
		}
		throw new IllegalArgumentException("'" + directory + "' not found!");
    }

	private File classFile() {
	    return classFileFor(getClass());
    }

	private File classFileFor(final Class klass) {
	    try {
	        return new File(klass.getResource("/" + klass.getName().replace('.', '/') + ".class").toURI());
        } catch (URISyntaxException e) {
        	throw new IllegalStateException(e);
        }
    }
	
	private File tempFile() throws IOException {
		return File.createTempFile("doctor", "db4o");
    }

}
