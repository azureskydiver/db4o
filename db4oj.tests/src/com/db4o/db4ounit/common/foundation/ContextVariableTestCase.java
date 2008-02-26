/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;

public class ContextVariableTestCase implements TestCase {

	public static void main(String[] args) {
		new ConsoleTestRunner(ContextVariableTestCase.class).run();
	}

	public void testSingleThread() {
		final ContextVariable variable = new ContextVariable();
		checkVariableBehavior(variable);
	}

	public void testMultiThread() {
		final ContextVariable variable = new ContextVariable();
		final Collection4 failures = new Collection4();
		variable.with("mine", new Runnable() {
			public void run() {
				final Thread[] threads = createThreads(variable, failures);
				startAll(threads);
				for (int i=0; i<10; ++i) {
					Assert.areEqual("mine", variable.value());
				}
				joinAll(threads);
			}
		});
		Assert.isNull(variable.value());
		Assert.isTrue(failures.isEmpty(), failures.toString());
	}

	private void joinAll(final Thread[] threads) {
		for (int i = 0; i < threads.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void startAll(final Thread[] threads) {
		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}
	}

	private Thread[] createThreads(final ContextVariable variable, final Collection4 failures) {
		final Thread[] threads = new Thread[5];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new Runnable() {
				public void run() {
					try {
						for (int i=0; i<10; ++i) {
							checkVariableBehavior(variable);
						}
					} catch (Exception failure) {
						synchronized (failures) {
							failures.add(failure);
						}
					}
				}
			});
		}
		return threads;
	}

	public void testTypeChecking() {

		final Runnable emptyBlock = new Runnable() {
			public void run() {
			}
		};

		final ContextVariable stringVar = new ContextVariable(String.class);
		stringVar.with("foo", emptyBlock);

		Assert.expect(IllegalArgumentException.class, new CodeBlock() {
			public void run() throws Throwable {
				stringVar.with(Boolean.TRUE, emptyBlock);
			}
		});

	}

	private void checkVariableBehavior(final ContextVariable variable) {
		Assert.isNull(variable.value());
		variable.with("foo", new Runnable() {
			public void run() {
				Assert.areEqual("foo", variable.value());
				variable.with("bar", new Runnable() {
					public void run() {
						Assert.areEqual("bar", variable.value());
					}
				});
				Assert.areEqual("foo", variable.value());
			}
		});
		Assert.isNull(variable.value());
	}

}
