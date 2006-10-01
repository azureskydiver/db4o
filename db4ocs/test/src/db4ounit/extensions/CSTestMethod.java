/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package db4ounit.extensions;

import java.lang.reflect.Method;

import com.db4o.ext.ExtObjectContainer;
import com.db4o.test.config.TestConfigure;

import db4ounit.TestMethod;

public class CSTestMethod extends TestMethod {

	private static ConcurrencyThread[] threads = new ConcurrencyThread[TestConfigure.CONCURRENCY_THREAD_COUNT];

	/**
	 * waits a certain thread to end.
	 * 
	 * @param seq
	 *            the thread id.
	 * @throws InterruptedException
	 */
	public static void waitThread(int seq) throws InterruptedException {
		threads[seq].join();
	}

	public CSTestMethod(Object instance, Method method) {
		super(instance, method);
	}

	/*
	 * Override invoke method to support concurrency test
	 * 
	 * @see db4ounit.TestMethod#invoke()
	 */
	protected void invoke() throws Exception {
		Db4oTestCase toTest = getSubject();
		Method method = getMethod();
		System.out.print(toTest.getClass().getName() + ":" + method.getName());
		Timer timer = new Timer();
		timer.start();
		try {
			if (method.getName().startsWith(TestConfigure.COCURRENCY_TEST_PREFIX)) {
				// concurrency test
				invokeConcurrencyMethod(toTest, method);
			} else {
				// normal test
				super.invoke();
			}
		} finally {
			timer.stop();
			System.out.println("(" + timer.elapsed() + " ms)");
		}
	}

	private void invokeConcurrencyMethod(Db4oTestCase toTest, Method method)
			throws Exception {
		Class[] parameters = method.getParameterTypes();
		boolean hasSequenceParameter = false;

		if (parameters.length == 2) // ExtObjectContainer, seq
			hasSequenceParameter = true;

		for (int i = 0; i < TestConfigure.CONCURRENCY_THREAD_COUNT; ++i) {
			if (hasSequenceParameter) {
				threads[i] = new ConcurrencyThread(toTest, method, i);
			} else {
				threads[i] = new ConcurrencyThread(toTest, method);
			}
		}
		// start threads simultaneously
		for (int i = 0; i < TestConfigure.CONCURRENCY_THREAD_COUNT; ++i) {
			threads[i].start();
		}
		// wait for the threads to end
		for (int i = 0; i < TestConfigure.CONCURRENCY_THREAD_COUNT; ++i) {
			threads[i].join();
		}
		// check if any of the threads ended abnormally
		for (int i = 0; i < TestConfigure.CONCURRENCY_THREAD_COUNT; ++i) {
			if (threads[i].fail) {
				throw threads[i].ex;
			}
		}
		// check test result
		checkConcurrencyMethod(toTest, method.getName());
	}

	private void checkConcurrencyMethod(Db4oTestCase toTest,
			String testMethodName) throws Exception {
		int testPrefixLength = TestConfigure.COCURRENCY_TEST_PREFIX.length();
		String subMethodName = testMethodName.substring(testPrefixLength);
		String checkMethodName = TestConfigure.COCURRENCY_CHECK_PREFIX
				+ subMethodName;
		Method checkMethod = null;
		try {
			Class[] types = { ExtObjectContainer.class };
			checkMethod = toTest.getClass().getDeclaredMethod(checkMethodName,
					types);
		} catch (Exception e) {
			// if checkMethod is not availble, return as success
			return;
		}
		// pass ExtObjectContainer as a param to check method
		ExtObjectContainer oc = toTest.db();
		Object[] args = { oc };
		try {
			checkMethod.invoke(toTest, args);
		} finally {
			oc.close();
		}
	}

	class ConcurrencyThread extends Thread {
		private Db4oTestCase toTest;

		private Method method;

		private int seq;

		private boolean hasSequenceParameter = false;

		private boolean fail = true;

		private Exception ex;

		ConcurrencyThread(Db4oTestCase toTest, Method method) {
			this.toTest = toTest;
			this.method = method;
		}

		ConcurrencyThread(Db4oTestCase toTest, Method method, int seq) {
			this(toTest, method);
			this.seq = seq;
			hasSequenceParameter = true;
		}

		public void run() {
			ExtObjectContainer oc = null;
			try {
				oc = toTest.db();
				Object[] args;
				if (hasSequenceParameter) {
					args = new Object[2];
					args[0] = oc;
					args[1] = new Integer(seq);
				} else {
					args = new Object[1];
					args[0] = oc;
				}
				method.invoke(toTest, (Object[]) args);
				fail = false;
			} catch (Exception ex) {
				// record the exception
				this.ex = ex;
			} finally {
				if (oc != null)
					oc.close();
			}
		}
	}

	@Override
	public Db4oTestCase getSubject() {
		return (Db4oTestCase) super.getSubject();
	}

}
