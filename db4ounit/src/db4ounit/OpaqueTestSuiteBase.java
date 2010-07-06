package db4ounit;

import com.db4o.foundation.*;

public abstract class OpaqueTestSuiteBase implements OpaqueTestSuite {

	private final Iterator4<Test> _tests;
	private TestExecutor _executor;
	
	public OpaqueTestSuiteBase(Iterator4<Test> tests) {
		_tests = tests;
	}
	
	public void executor(TestExecutor executor) {
		_executor = executor;
	}

	public String label() {
		// TODO Auto-generated method stub
		return null;
	}

	public void run() {
		TestExecutor executor = Environments.my(TestExecutor.class);
		try {
			suiteSetUp();
			while(_tests.moveNext()) {
				executor.execute(_tests.current());
			}
			suiteTearDown();
		}
		catch(Exception exc) {
			executor.fail(this, exc);
		}
	}
	
	protected abstract void suiteSetUp() throws Exception;
	protected abstract void suiteTearDown() throws Exception;

}
