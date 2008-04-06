package db4ounit.extensions;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

/**
 * @sharpen.ignore
 */
public class Db4oUnitTestMain extends UnitTestMain {
	public static void main(String[] args) throws Exception {
		new Db4oUnitTestMain().runTests(args);
	}

	private final Db4oFixture _fixture=new Db4oSolo();
	
	protected TestSuiteBuilder builder(Class clazz) {
		return new Db4oTestSuiteBuilder(_fixture,clazz);
	}
	
	protected Test testMethod(String className, String methodName)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		final Test test = super.testMethod(className, methodName);
		return new FixtureDecoration(test, Db4oFixtureVariable.FIXTURE_VARIABLE, _fixture);
	}
}
