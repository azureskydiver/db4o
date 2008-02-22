/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.tests.fixtures.dynamic;

import java.lang.reflect.*;

import com.db4o.foundation.*;

import db4ounit.*;

public abstract class FixtureBasedTestSuite implements TestSuiteBuilder {

	public abstract Class[] testUnits();

	public abstract FixtureProvider[] fixtureProviders();

	public Iterator4 build() {
		final FixtureProvider[] providers = fixtureProviders();
				
		final Iterable4 decorators = Iterators.map(Iterators.iterable(providers), new Function4() {
			public Object apply(final Object arg) {
				final FixtureProvider provider = (FixtureProvider)arg;
				return Iterators.map(provider, new Function4() {
					public Object apply(final Object fixture) {
						return new TestDecorator(){
							public Test decorate(Test test) {
								return new TestContext(test, provider.variable(), fixture);
							}
						};
					}
				});
			}
		});
		final Iterable4 product = Iterators.crossProduct(prepend(tests(), decorators));
		return Iterators.map(product.iterator(), new Function4() {
			public Object apply(Object arg) {
				Iterator4 tuple = ((Iterable4)arg).iterator();
				Test test = (Test)Iterators.next(tuple);
				return decorate(test, tuple);
			}
		});
	}

	private Iterable4 tests() {
		Iterator4 lazyTests = new ReflectionTestSuiteBuilder(testUnits()) {
			protected Test fromMethod(final Class clazz, final Method method) {
				return new Test() {
					public String getLabel() {
						return clazz.getName() + "." + method.getName();
					}
	
					public void run(TestResult result) {
						try {
							createTest(newInstance(clazz), method).run(result);
						} catch (Exception x) {
							result.testFailed(this, x);
						}
					}
				};
			}
		}.build();
		return Iterators.iterable(lazyTests);
	}

	private Iterable4[] prepend(Iterable4 tests, final Iterable4 decorators) {
		Object[] iterables = Iterators.toArray(decorators.iterator());
		final Iterable4[] source = new Iterable4[iterables.length+1];
		source[0] = tests;
		System.arraycopy(iterables, 0, source, 1, iterables.length);
		return source;
	}

	private Test decorate(Test test, Iterator4 decorators) {
		while (decorators.moveNext()) {
			test = ((TestDecorator)decorators.current()).decorate(test);
		}
		return test;
	}

}