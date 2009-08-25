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

	private final Db4oFixture _fixture = 
		new Db4oSolo();
//		new Db4oInMemory();
	
	@Override
	protected Iterable4 builder(Class clazz) {
		
		return Iterators.concat(new Iterable4[] {
				
				solo(clazz),
				embeddedCS(clazz),
				networkingCS(clazz),
				
//				defragSolo(clazz),
				
			}
		);
	}

	@SuppressWarnings("unused")
	private Db4oTestSuiteBuilder defragSolo(Class clazz) {
		return new Db4oTestSuiteBuilder(new Db4oDefragSolo(), clazz);
	}

	private Db4oTestSuiteBuilder networkingCS(Class clazz) {
		return new Db4oTestSuiteBuilder(Db4oFixtures.newNetworkingCS(), clazz);
	}

	private Db4oTestSuiteBuilder embeddedCS(Class clazz) {
		return new Db4oTestSuiteBuilder(Db4oFixtures.newEmbeddedCS(), clazz);
	}

	private Db4oTestSuiteBuilder solo(Class clazz) {
		return new Db4oTestSuiteBuilder(_fixture, clazz);
	}
	
	protected Test testMethod(String className, String methodName)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		final Test test = super.testMethod(className, methodName);
		return new TestWithFixture(test, Db4oFixtureVariable.FIXTURE_VARIABLE, _fixture);
	}
}
