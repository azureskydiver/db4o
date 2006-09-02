/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.lang.reflect.Method;

import db4ounit.TestMethod;

public class CSTestMethod extends TestMethod {

	static int CONCURRENCY_THREAD_COUNT = 10;

	static final String COCURRENCY_TEST_PREFIX = "conc";

	static final String COCURRENCY_CHECK_PREFIX = "check";

	static final int COCURRENCY_TEST_PREFIX_LENGTH = "conc".length();

	public CSTestMethod(Object instance, Method method) {
		super(instance, method);
	}

	/*
	 * Override invoke method to support concurrency test
	 * @see db4ounit.TestMethod#invoke()
	 */
	protected void invoke() throws Exception {
		Object toTest = getSubject();
		Method method = getMethod();
		if (method.getName().startsWith(COCURRENCY_TEST_PREFIX)) {
			// concurrency test
			invokeConcurrencyMethod(toTest, method);
		} else {
			// normal test
			super.invoke();
		}
	}

	private void invokeConcurrencyMethod(Object toTest, Method method)
			throws Exception {
		Class[] parameters = method.getParameterTypes();
		boolean hasArgs = false;
		if (parameters.length == 0) {
			// empty
		} else if (parameters.length == 1 && parameters[0] == Integer.TYPE) {
			hasArgs = true;
		} else {
			// wrong parameters type
			return;
		}

		ConcurrencyThread[] threads = new ConcurrencyThread[CONCURRENCY_THREAD_COUNT];
		for (int i = 0; i < CONCURRENCY_THREAD_COUNT; ++i) {
			if (hasArgs) {
				threads[i] = new ConcurrencyThread(toTest, method, i);
			} else {
				threads[i] = new ConcurrencyThread(toTest, method);
			}
		}
		// start threads simultaneously
		for (int i = 0; i < CONCURRENCY_THREAD_COUNT; ++i) {
			threads[i].start();
		}
		// wait for the threads to end
		for (int i = 0; i < CONCURRENCY_THREAD_COUNT; ++i) {
			threads[i].join();
		}
		// check if any of the threads ended abnormally
		for (int i = 0; i < CONCURRENCY_THREAD_COUNT; ++i) {
			if (threads[i].fail) {
				throw threads[i].ex;
			}
		}
		// check test result
		checkConcurrencyMethod(toTest, method.getName());
	}

	private void checkConcurrencyMethod(Object toTest, String testMethodName)
			throws Exception {
		String subMethodName = testMethodName
				.substring(COCURRENCY_TEST_PREFIX_LENGTH);
		String checkMethodName = COCURRENCY_CHECK_PREFIX + subMethodName;
		Method checkMethod = null;
		try {
			checkMethod = toTest.getClass().getDeclaredMethod(checkMethodName,
					(Class[]) null);
		} catch (Exception e) {
			// if checkMethod is not availble, return as success
			return;
		}
		checkMethod.invoke(toTest, (Object[]) null);
	}

	class ConcurrencyThread extends Thread {
		private Object toTest;

		private Method method;

		private int seq;

		private boolean hasArgs = false;

		private boolean fail = true;

		private Exception ex;

		ConcurrencyThread(Object toTest, Method method) {
			this.toTest = toTest;
			this.method = method;
		}

		ConcurrencyThread(Object toTest, Method method, int seq) {
			this(toTest, method);
			this.seq = seq;
			hasArgs = true;
		}

		public void run() {
			try {
				Integer[] args = null;
				if (hasArgs) {
					args = new Integer[1];
					args[0] = new Integer(seq);
				}
				method.invoke(toTest, (Object[])args);
				fail = false;
			} catch (Exception ex) {
				// empty
				this.ex = ex;
			}
		}
	}

}
