package db4ounit;

public abstract class TestCase implements Test {
	
	public String getName() {
		return getClass().getName();
	}
	
	public void run(TestResult result) {
		try {
			setUp();
			run();
		}
		catch(Exception exc) {
			result.fail(this, exc);
		}
		finally {
			try {
				tearDown();
			} catch (Exception exc) {
				result.fail(this, exc);
			}
		}
	}
	
	protected void setUp() throws Exception {}

	protected void tearDown() throws Exception {}

	protected abstract void run() throws Exception;
}
