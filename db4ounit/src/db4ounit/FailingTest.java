package db4ounit;

/**
 * A test that always fails with a specific exception.
 */
public class FailingTest implements Test {

	private final Throwable _error;
	private final String _label;

	public FailingTest(String label, Throwable error) {
		_label = label;
		_error = error;
	}

	public String getLabel() {
		return _label;
	}
	
	public Throwable error() {
		return _error;
	}

	public void run(TestResult result) {
		result.testFailed(this, _error);
	}

}