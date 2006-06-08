package db4ounit.db4o;


public abstract class Db4oTest {
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