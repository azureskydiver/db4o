package com.db4o.ta.instrumentation.test;

import java.io.*;

import com.db4o.foundation.io.*;
import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.filter.*;
import com.db4o.instrumentation.main.*;
import com.db4o.ta.*;
import com.db4o.ta.instrumentation.*;

import db4ounit.*;

public class TAFileEnhancerTestCase implements TestCase {
    
	private final static Class INSTRUMENTED_CLAZZ = ToBeInstrumentedWithFieldAccess.class;
	
	private final static Class NOT_INSTRUMENTED_CLAZZ = NotToBeInstrumented.class;

	public void test() throws Exception {
		final String srcDir = IO.mkTempDir("tafileinstr/source");
		final String targetDir = IO.mkTempDir("tafileinstr/target");

		final Class[] clazzes = { INSTRUMENTED_CLAZZ, NOT_INSTRUMENTED_CLAZZ };
		copyClassFilesTo(clazzes, srcDir);
		
		ClassFilter filter = new ByNameClassFilter(new String[]{ INSTRUMENTED_CLAZZ.getName() });
		Db4oFileEnhancer enhancer = new Db4oFileEnhancer(new InjectTransparentActivationEdit(filter));
		enhancer.enhance(srcDir, targetDir, new String[]{}, "");
		
		AssertingClassLoader loader = new AssertingClassLoader(new File(targetDir), clazzes);
		loader.assertAssignableFrom(Activatable.class, INSTRUMENTED_CLAZZ);
		loader.assertNotAssignableFrom(Activatable.class, NOT_INSTRUMENTED_CLAZZ);
	}

	private void copyClassFilesTo(final Class[] classes, final String toDir)
			throws IOException {
		for (int i = 0; i < classes.length; i++) {
			copyClassFile(classes[i], toDir);
		}
	}	

	private void copyClassFile(Class clazz, String toDir) throws IOException {
		File file = ClassFiles.fileForClass(clazz);
		String targetPath = Path4.combine(toDir, ClassFiles.classNameAsPath(clazz));
		File4.delete(targetPath);
		File4.copy(file.getCanonicalPath(), targetPath);
	}
}
