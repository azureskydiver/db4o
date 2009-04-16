package db4ounit.extensions;

import com.db4o.foundation.*;

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

	private final Db4oFixture _fixture=new Db4oInMemory();
	
	@Override
	protected Iterable4 builder(Class clazz) {
		final Db4oTestSuiteBuilder networkingCS = new Db4oTestSuiteBuilder(Db4oFixtures.newNetworkingCS(true), clazz);
		final Db4oTestSuiteBuilder inMemory = new Db4oTestSuiteBuilder(_fixture, clazz);
		final Db4oTestSuiteBuilder embeddedCS = new Db4oTestSuiteBuilder(Db4oFixtures.newEmbeddedCS(true), clazz);
		
		if (true) {
			return networkingCS;
		}
		if (true) {
			return inMemory;
		}
		
		return Iterators.concat(inMemory, embeddedCS, networkingCS);
	}
	
	protected Test testMethod(String className, String methodName)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		final Test test = super.testMethod(className, methodName);
		return new TestWithFixture(test, Db4oFixtureVariable.FIXTURE_VARIABLE, _fixture);
	}
}
