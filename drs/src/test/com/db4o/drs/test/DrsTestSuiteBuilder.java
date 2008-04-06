/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import com.db4o.foundation.*;

import db4ounit.ReflectionTestSuiteBuilder;

public class DrsTestSuiteBuilder extends ReflectionTestSuiteBuilder {
	
	private DrsFixturePair _fixtures;
	
	public DrsTestSuiteBuilder(DrsFixture a, DrsFixture b, Class clazz) {
		this(a, b, new Class[] { clazz });
	}
	
	public DrsTestSuiteBuilder(DrsFixture a, DrsFixture b, Class[] classes) {
		super(classes);
		_fixtures = new DrsFixturePair(a, b);
	}
	
	@Override
	protected Object withContext(Closure4 closure) {
		return DrsFixtureVariable.with(_fixtures, closure);
	}
}
