package com.db4o.ta.instrumentation.test;

import java.io.*;
import java.net.*;

import com.db4o.foundation.io.*;
import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.filter.*;
import com.db4o.instrumentation.main.*;
import com.db4o.ta.*;
import com.db4o.ta.instrumentation.*;
import com.db4o.test.util.*;

import db4ounit.*;

public class TAFileEnhancerTestCase implements TestCase {
    
	private final static Class INSTRUMENTED_CLAZZ = ToBeInstrumentedWithFieldAccess.class;
	
	private final static Class NOT_INSTRUMENTED_CLAZZ = NotToBeInstrumented.class;

	public void test() throws Exception {
		final String srcDir = mkTempDir("tafileinstr/source");
		final String targetDir = mkTempDir("tafileinstr/target");

		final Class[] clazzes = { INSTRUMENTED_CLAZZ, NOT_INSTRUMENTED_CLAZZ };
		copyClassFilesTo(clazzes, srcDir);
		
		ClassFilter filter = new ByNameClassFilter(new String[]{ INSTRUMENTED_CLAZZ.getName() });
		Db4oFileEnhancer enhancer = new Db4oFileEnhancer(new InjectTransparentActivationEdit(filter));
		enhancer.enhance(srcDir, targetDir, new String[]{}, "");
		
		ExcludingClassLoader excludingLoader = new ExcludingClassLoader(getClass().getClassLoader(), clazzes);
		URLClassLoader loader = new URLClassLoader(new URL[] { new File(targetDir).toURL() }, excludingLoader);
		
		Class instrumented = loader.loadClass(INSTRUMENTED_CLAZZ.getName());
		Assert.isTrue(Activatable.class.isAssignableFrom(instrumented));
		Class uninstrumented = loader.loadClass(NOT_INSTRUMENTED_CLAZZ.getName());
		Assert.isFalse(Activatable.class.isAssignableFrom(uninstrumented));
	}

	private void copyClassFilesTo(final Class[] classes, final String toDir)
			throws IOException {
		for (int i = 0; i < classes.length; i++) {
			copyClassFile(classes[i], toDir);
		}
	}

	private String mkTempDir(String path) {
		final String tempDir = Path4.combine(Path4.getTempPath(), path);
		File4.mkdirs(tempDir);
		return tempDir;
	}

	private void copyClassFile(Class clazz, String toDir) throws IOException {
		File file = fileForClass(clazz);
		String targetPath = Path4.combine(toDir, clazz.getName().replace('.', '/') + ".class");
		File4.delete(targetPath);
		File4.copy(file.getCanonicalPath(), targetPath);
	}

	private File fileForClass(Class clazz) throws IOException {
		String clazzName = clazz.getName();
		int dotIdx = clazzName.lastIndexOf('.');
		String simpleName = clazzName.substring(dotIdx + 1);
		URL url = clazz.getResource(simpleName + ".class");
		return new File(url.getFile());
	}
}
