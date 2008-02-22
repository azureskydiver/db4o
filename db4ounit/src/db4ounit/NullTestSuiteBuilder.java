package db4ounit;

import com.db4o.foundation.*;

public class NullTestSuiteBuilder implements TestSuiteBuilder {
	
	private Iterator4 _suite;

	public NullTestSuiteBuilder(Iterator4 suite) {
		_suite = suite;
	}

	public Iterator4 build() {
		return _suite;
	}

}
