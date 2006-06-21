package db4ounit.db4o;

import db4ounit.TestLifeCycle;

public class Db4oTestCase implements TestLifeCycle {
	private Db4oFixture _fixture;
	
	public void fixture(Db4oFixture fixture) {
		_fixture = fixture;
	}

	public Db4oFixture fixture() {
		return _fixture;
	}
	
	public void setUp() throws Exception {
		configure();
		_fixture.open();
		store();
	}
	
	public void tearDown() throws Exception {
		_fixture.close();
	}

	protected void configure() {}
	
	protected void store() {}
}
