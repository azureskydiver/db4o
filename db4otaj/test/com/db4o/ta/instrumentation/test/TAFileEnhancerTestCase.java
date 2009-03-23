/* Copyright (C) 2009  db4objects Inc.   http://www.db4o.com */
package com.db4o.ta.instrumentation.test;

import java.net.*;
import java.util.*;

import EDU.purdue.cs.bloat.editor.*;

import com.db4o.db4ounit.common.ta.*;
import com.db4o.foundation.*;
import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.main.*;
import com.db4o.internal.*;
import com.db4o.ta.*;
import com.db4o.ta.instrumentation.test.collections.*;
import com.db4o.ta.instrumentation.test.data.*;

import db4ounit.*;

public class TAFileEnhancerTestCase extends TAFileEnhancerTestCaseBase {
    
	private static final String INSTANCE_FACTORY_METHOD_NAME = "collectionInstance";
	private final static Class INSTRUMENTED_CLAZZ = ToBeInstrumentedWithFieldAccess.class;
	private final static Class NOT_INSTRUMENTED_CLAZZ = NotToBeInstrumented.class;
	private final static Class EXTERNAL_INSTRUMENTED_CLAZZ = ToBeInstrumentedWithExternalFieldAccess.class;
	private final static Class INSTRUMENTED_OUTER_CLAZZ = ToBeInstrumentedOuter.class;
	private final static Class INSTRUMENTED_INNER_CLAZZ = getAnonymousInnerClass(INSTRUMENTED_OUTER_CLAZZ);
	private final static Class LIST_CLIENT_CLAZZ = ArrayListClient.class;
	private final static Class MAP_CLIENT_CLAZZ = HashMapClient.class;
	private final static Class CUSTOM_LIST_CLAZZ = CustomArrayList.class;

	final static Class[] INSTRUMENTED_CLASSES = new Class[] { 
		INSTRUMENTED_CLAZZ, 
		EXTERNAL_INSTRUMENTED_CLAZZ, 
		INSTRUMENTED_OUTER_CLAZZ, 
		INSTRUMENTED_INNER_CLAZZ, 
		LIST_CLIENT_CLAZZ,
		MAP_CLIENT_CLAZZ,
		CUSTOM_LIST_CLAZZ,
		MyArrayList.class,
	};

	private final static Class[] NOT_INSTRUMENTED_CLASSES = new Class[] { 
		NOT_INSTRUMENTED_CLAZZ, 
	};

	private final static Class[] INPUT_CLASSES = (Class[])Arrays4.merge(INSTRUMENTED_CLASSES, NOT_INSTRUMENTED_CLASSES, Class.class);
	
	private static Class getAnonymousInnerClass(Class clazz) {
		try {
			return Class.forName(clazz.getName() + "$1");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected Class[] inputClasses() {
		return INPUT_CLASSES;
	}

	protected Class[] instrumentedClasses() {
		return INSTRUMENTED_CLASSES;
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
		final List list = (List)Reflection4.invoke(client, INSTANCE_FACTORY_METHOD_NAME);
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
		Procedure4<Object> proc = new Procedure4<Object>() {
			public void apply(Object arg) {
				((List)arg).iterator();
			}
		};
		assertActivatorInvocations(LIST_CLIENT_CLAZZ, proc, 1,0);
	}

	public void testArrayListPersistence() throws Exception {
		Procedure4<Object> proc = new Procedure4<Object>() {
			public void apply(Object arg) {
				((List)arg).add("foo");
			}
		};
		assertActivatorInvocations(LIST_CLIENT_CLAZZ, proc, 0,1);
	}

	public void testHashMapActivation() throws Exception {
		Procedure4<Object> proc = new Procedure4<Object>() {
			public void apply(Object arg) {
				((Map)arg).keySet();
			}
		};
		assertActivatorInvocations(MAP_CLIENT_CLAZZ, proc, 1,0);
	}

	public void testHashMapPersistence() throws Exception {
		Procedure4<Object> proc = new Procedure4<Object>() {
			public void apply(Object arg) {
				((Map)arg).put("foo", "bar");
			}
		};
		assertActivatorInvocations(MAP_CLIENT_CLAZZ, proc, 0,1);
	}

	private void assertActivatorInvocations(Class clientClass, Procedure4<Object> proc,
			int expectedReads, int expectedWrites) throws Exception,
			MalformedURLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		enhance();
		AssertingClassLoader loader = newAssertingClassLoader();		
		Activatable client = (Activatable) loader.newInstance(clientClass);
		MockActivator clientActivator = MockActivator.activatorFor(client);
		final Object collection = Reflection4.invoke(client, INSTANCE_FACTORY_METHOD_NAME);
		assertReadsWrites(1, 0, clientActivator);
		MockActivator collectionActivator = MockActivator.activatorFor((Activatable)collection);
		proc.apply(collection);
		assertReadsWrites(expectedReads, expectedWrites, collectionActivator);
	}


	private void instantiateInnerClass(AssertingClassLoader loader) throws Exception {
		Class outerClazz = loader.loadClass(INSTRUMENTED_OUTER_CLAZZ);
		Object outerInst = outerClazz.newInstance();
		outerClazz.getDeclaredMethod("foo", new Class[]{}).invoke(outerInst, new Object[]{});
	}
	
}
