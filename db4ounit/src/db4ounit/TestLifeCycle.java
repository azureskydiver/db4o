package db4ounit;

/**
 * For test cases that need setUp/tearDown support.
 */
public interface TestLifeCycle {
	
	public void setUp() throws Exception;

	public void tearDown() throws Exception;
}
