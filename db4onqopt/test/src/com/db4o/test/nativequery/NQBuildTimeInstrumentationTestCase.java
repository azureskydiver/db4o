package com.db4o.test.nativequery;

import java.io.*;
import java.net.*;

import com.db4o.foundation.io.*;
import com.db4o.instrumentation.ant.Db4oFileEnhancerAntTask;
import com.db4o.instrumentation.main.*;
import com.db4o.nativequery.main.*;
import com.db4o.nativequery.optimization.*;
import com.db4o.query.*;
import com.db4o.test.util.*;

import db4ounit.*;

public class NQBuildTimeInstrumentationTestCase implements TestLifeCycle {

	private final static String SRC_DIR = Path4.combine(Path4.getTempPath(), "nqfileinstr/source");
	private final static String TARGET_DIR = Path4.combine(Path4.getTempPath(), "nqfileinstr/target");
	private final static Class[] CLAZZES = { ToBeInstrumented.class, NotToBeInstrumented.class };


	public void testFileEnhancer() throws Exception {		
		Db4oFileEnhancer enhancer = new Db4oFileEnhancer(new TranslateNQToSODAEdit());
		enhancer.enhance(SRC_DIR, TARGET_DIR, new String[]{}, "");		
		assertInstrumented();
	}

	public void testAntTask() throws Exception {		
		Db4oFileEnhancerAntTask antTask = new Db4oFileEnhancerAntTask();
		antTask.setSrcdir(SRC_DIR);
		antTask.setTargetdir(TARGET_DIR);
		antTask.add(new NQAntClassEditFactory());
		antTask.execute();
		assertInstrumented();
	}

	private void assertInstrumented() throws MalformedURLException,
			ClassNotFoundException, NoSuchMethodException {
		ExcludingClassLoader excludingLoader = new ExcludingClassLoader(getClass().getClassLoader(), CLAZZES);
		URLClassLoader loader = new URLClassLoader(new URL[] { new File(TARGET_DIR).toURI().toURL() }, excludingLoader);
		
		Class instrumented = loader.loadClass(ToBeInstrumented.class.getName());
		final Class[] queryClassSig = new Class[]{Query.class};
		Assert.isNotNull(instrumented.getDeclaredMethod(NativeQueryEnhancer.OPTIMIZE_QUERY_METHOD_NAME, queryClassSig));
		final Class uninstrumented = loader.loadClass(NotToBeInstrumented.class.getName());
		Assert.expect(NoSuchMethodException.class, new CodeBlock() {
			public void run() throws Throwable {
				uninstrumented.getDeclaredMethod(NativeQueryEnhancer.OPTIMIZE_QUERY_METHOD_NAME, queryClassSig);
			}
		});
	}

	private void copyClassFile(String srcDir, Class clazz) throws IOException {
		File file = fileForClass(clazz);
		String targetPath = Path4.combine(srcDir, clazz.getName().replace('.', '/') + ".class");
		File4.delete(targetPath);
		File4.copy(file.getCanonicalPath(), targetPath);
	}

	private File fileForClass(Class clazz) throws IOException {
		String clazzName = clazz.getName();
		int dotIdx = clazzName.lastIndexOf('.');
		String simpleName = clazzName.substring(dotIdx + 1);
		URL url = clazz.getResource(simpleName + ".class");
		return new File(url.getPath());
	}

	public void setUp() throws Exception {
		File4.mkdirs(SRC_DIR);
		File4.mkdirs(TARGET_DIR);		
		for (int clazzIdx = 0; clazzIdx < CLAZZES.length; clazzIdx++) {
			copyClassFile(SRC_DIR, CLAZZES[clazzIdx]);
		}
	}

	public void tearDown() throws Exception {
	}
}
