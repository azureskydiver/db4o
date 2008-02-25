package db4ounit.extensions;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

/**
 * @sharpen.ignore
 */
public class Db4oUnitTestMain extends UnitTestMain {
	public static void main(String[] args) throws Exception {
		new Db4oUnitTestMain().runTests(args);
	}

	private Db4oFixture _fixture=new Db4oSolo();
	
	protected TestSuiteBuilder builder(Class[] clazzes) {
		return new Db4oTestSuiteBuilder(_fixture,clazzes);
	}
}
