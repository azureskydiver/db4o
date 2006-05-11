package com.db4o.test.unit.db4o;

import com.db4o.test.unit.*;

public abstract class Db4oTest extends Assert {
	private Db4oFixture _fixture;
	
	public Db4oTest() {
	}
	
	public void fixture(Db4oFixture fixture) {
		_fixture=fixture;
	}
	
	protected Db4oFixture fixture() {
		return _fixture;
	}
	
	protected void configure() {}
	
	protected void store() {}
}