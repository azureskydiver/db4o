/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package db4ounit.extensions.concurrency;

import java.lang.reflect.*;

import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class ConcurrencyTestMethod extends TestMethod {

	private static Thread[] threads = new Thread[ConcurrenyConst.CONCURRENCY_THREAD_COUNT];
	
	private Exception[] failures = new Exception[ConcurrenyConst.CONCURRENCY_THREAD_COUNT];

	public ConcurrencyTestMethod(Object instance, Method method) {
		super(instance, method, Db4oFixtureLabelProvider.DEFAULT);
	}

	/*
	 * Override invoke method to support concurrency test
	 * 
	 * @see db4ounit.TestMethod#invoke()
	 */
	protected void invoke() throws Exception {
		AbstractDb4oTestCase toTest = (AbstractDb4oTestCase) getSubject();
		Method method = getMethod();
		invokeConcurrencyMethod(toTest, method);
	}

	private void invokeConcurrencyMethod(AbstractDb4oTestCase toTest, Method method)
			throws Exception {
		Class[] parameters = method.getParameterTypes();
		boolean hasSequenceParameter = false;

		if (parameters.length == 2) // ExtObjectContainer, seq
			hasSequenceParameter = true;

		for (int i = 0; i < ConcurrenyConst.CONCURRENCY_THREAD_COUNT; ++i) {
			threads[i] = new Thread(new RunnableTestMethod(toTest, method, i,hasSequenceParameter));
			failures[i] = null;
		}
		// start threads simultaneously
		for (int i = 0; i < ConcurrenyConst.CONCURRENCY_THREAD_COUNT; ++i) {
			threads[i].start();
		}
		// wait for the threads to end
		for (int i = 0; i < ConcurrenyConst.CONCURRENCY_THREAD_COUNT; ++i) {
			threads[i].join();
		}
		// check if any of the threads ended abnormally
		for (int i = 0; i < ConcurrenyConst.CONCURRENCY_THREAD_COUNT; ++i) {
			if (failures[i] != null) {
				// TODO: show all failures by throwing another kind of exception.
				throw failures[i];
			}
		}
		// check test result
		checkConcurrencyMethod(toTest, method.getName());
	}

	private void checkConcurrencyMethod(AbstractDb4oTestCase toTest,
			String testMethodName) throws Exception {
		int testPrefixLength = ConcurrenyConst.COCURRENCY_TEST_PREFIX.length();
		String subMethodName = testMethodName.substring(testPrefixLength);
		String checkMethodName = ConcurrenyConst.COCURRENCY_CHECK_PREFIX
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
		ExtObjectContainer oc = toTest.fixture().db();
		Object[] args = { oc };
		try {
			checkMethod.invoke(toTest, args);
		} finally {
			oc.close();
		}
	}

	class RunnableTestMethod implements Runnable {
		private AbstractDb4oTestCase toTest;

		private Method method;

		private int seq;

		private boolean showSeq;

		RunnableTestMethod(AbstractDb4oTestCase toTest, Method method) {
			this.toTest = toTest;
			this.method = method;
		}

		RunnableTestMethod(AbstractDb4oTestCase toTest, Method method, int seq, boolean showSeq) {
			this.toTest = toTest;
			this.method = method;
			this.seq = seq;
			this.showSeq = showSeq;
		}

		public void run() {
			ExtObjectContainer oc = null;
			try {
				oc = openNewClient(toTest);
				Object[] args;
				if (showSeq) {
					args = new Object[2];
					args[0] = oc;
					args[1] = new Integer(seq);
				} else {
					args = new Object[1];
					args[0] = oc;
				}
				method.invoke(toTest, (Object[]) args);
			} catch (Exception e) {
				failures[seq] = e;
			} finally {
				if (oc != null)
					oc.close();
			}
		}
	}
	
	private ExtObjectContainer openNewClient(AbstractDb4oTestCase toTest) {
		return ((Db4oMultiClient)toTest.fixture()).openNewClient();
	}

}
