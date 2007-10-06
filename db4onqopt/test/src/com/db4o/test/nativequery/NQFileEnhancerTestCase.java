package com.db4o.test.nativequery;

import java.io.*;
import java.net.*;

import com.db4o.foundation.io.*;
import com.db4o.instrumentation.*;
import com.db4o.nativequery.main.*;
import com.db4o.nativequery.optimization.*;
import com.db4o.query.*;
import com.db4o.test.util.*;

import db4ounit.*;

public class NQFileEnhancerTestCase implements TestCase {

	public void test() throws Exception {
		final String srcDir = Path4.combine(Path4.getTempPath(), "nqfileinstr/source");
		File4.mkdirs(srcDir);
		final String targetDir = Path4.combine(Path4.getTempPath(), "nqfileinstr/target");
		File4.mkdirs(targetDir);

		final Class[] clazzes = { ToBeInstrumented.class, NotToBeInstrumented.class };
		
		for (int clazzIdx = 0; clazzIdx < clazzes.length; clazzIdx++) {
			copyClassFile(srcDir, clazzes[clazzIdx]);
		}
		
		Db4oFileEnhancer enhancer = new Db4oFileEnhancer(new TranslateNQToSODAEdit());
		enhancer.enhance(srcDir, targetDir, new String[]{srcDir}, "");
		
		ExcludingClassLoader excludingLoader = new ExcludingClassLoader(getClass().getClassLoader(), clazzes);
		URLClassLoader loader = new URLClassLoader(new URL[] { new File(targetDir).toURI().toURL() }, excludingLoader);
		
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
		return new File(url.toURI());
	}
}
