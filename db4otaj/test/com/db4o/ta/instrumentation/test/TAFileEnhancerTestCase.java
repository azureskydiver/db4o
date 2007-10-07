package com.db4o.ta.instrumentation.test;

import java.io.*;
import java.net.*;

import com.db4o.foundation.io.*;
import com.db4o.instrumentation.*;
import com.db4o.query.*;
import com.db4o.ta.*;
import com.db4o.ta.instrumentation.*;
import com.db4o.test.util.*;

import db4ounit.*;

public class TAFileEnhancerTestCase implements TestCase {
	
	private final static Class INSTRUMENTED_CLAZZ = ToBeInstrumentedWithFieldAccess.class;
	
	private final static Class NOT_INSTRUMENTED_CLAZZ = NotToBeInstrumented.class;

	public void test() throws Exception {
		final String srcDir = Path4.combine(Path4.getTempPath(), "tafileinstr/source");
		File4.mkdirs(srcDir);
		final String targetDir = Path4.combine(Path4.getTempPath(), "tafileinstr/target");
		File4.mkdirs(targetDir);

		final Class[] clazzes = { INSTRUMENTED_CLAZZ, NOT_INSTRUMENTED_CLAZZ };
		
		for (int clazzIdx = 0; clazzIdx < clazzes.length; clazzIdx++) {
			copyClassFile(srcDir, clazzes[clazzIdx]);
		}
		
		ClassFilter filter = new ByNameClassFilter(new String[]{ INSTRUMENTED_CLAZZ.getName() });
		Db4oFileEnhancer enhancer = new Db4oFileEnhancer(new InjectTransparentActivationEdit(filter));
		enhancer.enhance(srcDir, targetDir, new String[]{}, "");
		
		ExcludingClassLoader excludingLoader = new ExcludingClassLoader(getClass().getClassLoader(), clazzes);
		URLClassLoader loader = new URLClassLoader(new URL[] { new File(targetDir).toURI().toURL() }, excludingLoader);
		
		Class instrumented = loader.loadClass(INSTRUMENTED_CLAZZ.getName());
		Assert.isTrue(Activatable.class.isAssignableFrom(instrumented));
		Class uninstrumented = loader.loadClass(NOT_INSTRUMENTED_CLAZZ.getName());
		Assert.isFalse(Activatable.class.isAssignableFrom(uninstrumented));
	}

	private void copyClassFile(String srcDir, Class clazz)
			throws URISyntaxException, IOException {
		File file = fileForClass(clazz);
		String targetPath = Path4.combine(srcDir, clazz.getName().replace('.', '/') + ".class");
		File4.delete(targetPath);
		File4.copy(file.getCanonicalPath(), targetPath);
	}

	private File fileForClass(Class clazz) throws URISyntaxException,
			IOException {
		String clazzName = clazz.getName();
		int dotIdx = clazzName.lastIndexOf('.');
		String simpleName = clazzName.substring(dotIdx + 1);
		URL url = clazz.getResource(simpleName + ".class");
		// FIXME: toURI is since 1.4
		return new File(url.toURI());
	}
}
