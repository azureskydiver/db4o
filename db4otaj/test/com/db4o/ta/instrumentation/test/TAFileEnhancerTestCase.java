package com.db4o.ta.instrumentation.test;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

import EDU.purdue.cs.bloat.editor.*;

import com.db4o.db4ounit.common.ta.*;
import com.db4o.foundation.io.*;
import com.db4o.instrumentation.classfilter.*;
import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.main.*;
import com.db4o.internal.*;
import com.db4o.ta.*;
import com.db4o.ta.instrumentation.*;
import com.db4o.ta.instrumentation.test.data.*;

import db4ounit.*;

public class TAFileEnhancerTestCase implements TestCase, TestLifeCycle {
    
	private final static Class INSTRUMENTED_CLAZZ = ToBeInstrumentedWithFieldAccess.class;
	private final static Class NOT_INSTRUMENTED_CLAZZ = NotToBeInstrumented.class;
	private final static Class EXTERNAL_INSTRUMENTED_CLAZZ = ToBeInstrumentedWithExternalFieldAccess.class;
	private final static Class INSTRUMENTED_OUTER_CLAZZ = ToBeInstrumentedOuter.class;
	private final static Class INSTRUMENTED_INNER_CLAZZ = getAnonymousInnerClass(INSTRUMENTED_OUTER_CLAZZ);
	private final static Class LIST_CLIENT_CLAZZ = ArrayListClient.class;

	private final static Class[] INSTRUMENTED_CLASSES = new Class[] { 
		INSTRUMENTED_CLAZZ, 
		EXTERNAL_INSTRUMENTED_CLAZZ, 
		INSTRUMENTED_OUTER_CLAZZ, 
		INSTRUMENTED_INNER_CLAZZ, 
		LIST_CLIENT_CLAZZ,
	};

	private final static Class[] NOT_INSTRUMENTED_CLASSES = new Class[] { 
		NOT_INSTRUMENTED_CLAZZ, 
	};

	private final static Class[] INPUT_CLASSES = (Class[])merge(INSTRUMENTED_CLASSES, NOT_INSTRUMENTED_CLASSES, Class.class);
	
	private static Class getAnonymousInnerClass(Class clazz) {
		try {
			return Class.forName(clazz.getName() + "$1");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String srcDir;
	
	private String targetDir;	
	
	public void setUp() throws Exception {
		srcDir = IO.mkTempDir("tafileinstr/source");
		targetDir = IO.mkTempDir("tafileinstr/target");
		copyClassFilesTo(
			INPUT_CLASSES,
			srcDir);
	}
	
	public void test() throws Exception {
		enhance();
		
		AssertingClassLoader loader = newAssertingClassLoader();
		for (int instrumentedIdx = 0; instrumentedIdx < INSTRUMENTED_CLASSES.length; instrumentedIdx++) {
			loader.assertAssignableFrom(Activatable.class, INSTRUMENTED_CLASSES[instrumentedIdx]);
		}
		for (int notInstrumentedIdx = 0; notInstrumentedIdx < NOT_INSTRUMENTED_CLASSES.length; notInstrumentedIdx++) {
			loader.assertNotAssignableFrom(Activatable.class, NOT_INSTRUMENTED_CLASSES[notInstrumentedIdx]);
		}
		instantiateInnerClass(loader);
	}
	
	public void testMethodInstrumentation() throws Exception {
		enhance();
		
		AssertingClassLoader loader = newAssertingClassLoader();
		
		Activatable instrumented = (Activatable) loader.newInstance(INSTRUMENTED_CLAZZ);
		MockActivator activator = MockActivator.activatorFor(instrumented);
		Reflection4.invoke(instrumented, "setInt", Integer.TYPE, new Integer(42));
		assertReadsWrites(0, 1, activator);
	}
	
	public void testExternalFieldAccessInstrumentation() throws Exception {
		enhance();
		
		AssertingClassLoader loader = newAssertingClassLoader();
		
		Activatable server = (Activatable) loader.newInstance(INSTRUMENTED_CLAZZ);
		Object client = loader.newInstance(EXTERNAL_INSTRUMENTED_CLAZZ);
		MockActivator activator = MockActivator.activatorFor(server);
		Reflection4.invoke(client, "accessExternalField", server.getClass(), server);
		assertReadsWrites(0, 1, activator);
	}
	
	
	public void testExceptionsAreBubbledUp() throws Exception {
		
		final RuntimeException exception = new RuntimeException();
		
		final Throwable thrown = Assert.expect(RuntimeException.class, new CodeBlock() {
			public void run() throws Exception {
				new Db4oFileInstrumentor(new BloatClassEdit() {
					public InstrumentationStatus enhance(
							ClassEditor ce,
							ClassLoader origLoader,
							BloatLoaderContext loaderContext) {
						
						throw exception;
						
					}
				}).enhance(srcDir, targetDir, new String[] {});
			}
		});
		
		Assert.areSame(exception, thrown);
			
	}

	public void testArrayListActivationWithException() throws Exception {
		enhance();
		AssertingClassLoader loader = newAssertingClassLoader();		
		Activatable client = (Activatable) loader.newInstance(LIST_CLIENT_CLAZZ);
		MockActivator clientActivator = MockActivator.activatorFor(client);
		final List list = (List)Reflection4.invoke(client, "list");
		assertReadsWrites(1, 0, clientActivator);
		MockActivator listActivator = MockActivator.activatorFor((Activatable)list);
		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.get(0);
			}
		});
		assertReadsWrites(1, 0, listActivator);
	}

	public void testArrayListActivation() throws Exception {
		enhance();
		AssertingClassLoader loader = newAssertingClassLoader();		
		Activatable client = (Activatable) loader.newInstance(LIST_CLIENT_CLAZZ);
		MockActivator clientActivator = MockActivator.activatorFor(client);
		final List list = (List)Reflection4.invoke(client, "list");
		assertReadsWrites(1, 0, clientActivator);
		MockActivator listActivator = MockActivator.activatorFor((Activatable)list);
		list.iterator();
		assertReadsWrites(1, 0, listActivator);
	}

	public void testArrayListPersistence() throws Exception {
		enhance();
		AssertingClassLoader loader = newAssertingClassLoader();		
		Activatable client = (Activatable) loader.newInstance(LIST_CLIENT_CLAZZ);
		MockActivator clientActivator = MockActivator.activatorFor(client);
		List list = (List)Reflection4.invoke(client, "list");
		assertReadsWrites(1, 0, clientActivator);
		MockActivator listActivator = MockActivator.activatorFor((Activatable)list);
		list.add("foo");
		assertReadsWrites(0, 1, listActivator);
	}

	public void tearDown() throws Exception {
		deleteFiles();
	}

	private void instantiateInnerClass(AssertingClassLoader loader) throws Exception {
		Class outerClazz = loader.loadClass(INSTRUMENTED_OUTER_CLAZZ);
		Object outerInst = outerClazz.newInstance();
		outerClazz.getDeclaredMethod("foo", new Class[]{}).invoke(outerInst, new Object[]{});
	}

	private AssertingClassLoader newAssertingClassLoader() throws MalformedURLException {
		return new AssertingClassLoader(new File(targetDir), INPUT_CLASSES);
	}

	private void enhance() throws Exception {
		
		String[] filterClassNames = new String[INSTRUMENTED_CLASSES.length];
		for (int instrumentedIdx = 0; instrumentedIdx < INSTRUMENTED_CLASSES.length; instrumentedIdx++) {
			filterClassNames[instrumentedIdx] = INSTRUMENTED_CLASSES[instrumentedIdx].getName();
		}
		ClassFilter filter = new ByNameClassFilter(filterClassNames);
		Db4oFileInstrumentor enhancer = new Db4oFileInstrumentor(new InjectTransparentActivationEdit(filter));
		enhancer.enhance(srcDir, targetDir, new String[]{});
	}

	private void assertReadsWrites(int expectedReads, int expectedWrites, MockActivator activator) {
		Assert.areEqual(expectedReads, activator.readCount());
		Assert.areEqual(expectedWrites, activator.writeCount());
	}

	private void deleteFiles() {
		deleteDirectory(srcDir);
		deleteDirectory(targetDir);
	}

	private void deleteDirectory(String dirPath) {
		if(!File4.exists(dirPath)) {
			return;
		}
		Directory4.delete(dirPath, true);
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
	
	private static Object[] merge(Object[] a, Object[] b, Class arrayType) {
		Object[] merged = (Object[])Array.newInstance(arrayType, a.length + b.length);
		System.arraycopy(a, 0, merged, 0, a.length);
		System.arraycopy(b, 0, merged, a.length, b.length);
		return merged;
	}

}
