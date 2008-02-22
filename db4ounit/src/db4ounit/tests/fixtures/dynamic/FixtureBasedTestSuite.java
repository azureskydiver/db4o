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
				final ContextVariable variable = provider.variable();
				return Iterators.map(provider, new Function4() {
					public Object apply(final Object fixture) {
						return new FixtureDecorator(variable, fixture);
					}
				});
			}
		});
		final Iterable4 testsXdecorators = Iterators.crossProduct(new Iterable4[] {
			tests(),
			Iterators.crossProduct(decorators)
		});
		return Iterators.map(testsXdecorators, new Function4() {
			public Object apply(Object arg) {
				Iterator4 tuple = ((Iterable4)arg).iterator();
				Test test = (Test)Iterators.next(tuple);
				Iterable4 decorators = (Iterable4)Iterators.next(tuple);
				return decorate(test, decorators.iterator());
			}
		}).iterator();
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
	
	private Test decorate(Test test, Iterator4 decorators) {
		while (decorators.moveNext()) {
			test = ((TestDecorator)decorators.current()).decorate(test);
		}
		return test;
	}

}