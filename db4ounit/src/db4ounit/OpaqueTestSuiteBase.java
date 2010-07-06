package db4ounit;

import com.db4o.foundation.*;

public abstract class OpaqueTestSuiteBase implements Test {

	private final Closure4<Iterator4<Test>> _tests;
	
	public OpaqueTestSuiteBase(Closure4<Iterator4<Test>> tests) {
		_tests = tests;
	}

	public void run() {
		TestExecutor executor = Environments.my(TestExecutor.class);
		Iterator4<Test> tests = _tests.run();
		try {
			suiteSetUp();
			while(tests.moveNext()) {
				executor.execute(tests.current());
			}
			suiteTearDown();
		}
		catch(Exception exc) {
			executor.fail(this, exc);
		}
	}
	
	public boolean isLeafTest() {
		return false;
	}
	
	protected abstract void suiteSetUp() throws Exception;
	protected abstract void suiteTearDown() throws Exception;

}
