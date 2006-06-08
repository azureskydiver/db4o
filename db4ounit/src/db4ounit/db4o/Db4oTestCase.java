package db4ounit.db4o;

import db4ounit.*;

public class Db4oTestCase extends BaseTestCase {
	private Db4oFixture _fixture;
	
	public void fixture(Db4oFixture fixture) {
		_fixture=fixture;
	}

	protected Db4oFixture fixture() {
		return _fixture;
	}
	
	protected void setUp() throws Exception {
		configure();
		_fixture.open();
		store();
	}
	
	protected void tearDown() throws Exception {
		_fixture.close();
	}

	protected void configure() {}
	
	protected void store() {}
}
