/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.test.replication.db4ounit;

import db4ounit.ReflectionTestSuiteBuilder;

public class DrsTestSuiteBuilder extends ReflectionTestSuiteBuilder {
	
	private DrsFixture _a;
	private DrsFixture _b;

	public DrsTestSuiteBuilder(DrsFixture a, DrsFixture b, Class clazz) {
		super(clazz);
		a(a);
		b(b);
	}
	
	public DrsTestSuiteBuilder(DrsFixture a, DrsFixture b, Class[] classes) {
		super(classes);
		a(a);
		b(b);
	}
	
	private void a(DrsFixture fixture) {
		if (null == fixture) throw new IllegalArgumentException("fixture");
		_a = fixture;
	}

	private void b(DrsFixture fixture) {
		if (null == fixture) throw new IllegalArgumentException("fixture");
		_b = fixture;
	}

	protected Object newInstance(Class clazz) {
		Object instance = super.newInstance(clazz);
		if (instance instanceof DrsTestCase) {
			DrsTestCase testCase = (DrsTestCase) instance;
			testCase.a(_a);
			testCase.b(_b);
		}
		return instance;
	}
}
