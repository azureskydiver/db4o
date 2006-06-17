/**
 * 
 */
package db4ounit.db4o;

import db4ounit.ReflectionTestSuiteBuilder;

public class Db4oTestSuiteBuilder extends ReflectionTestSuiteBuilder {
	
	private Db4oFixture _fixture;
	
	public Db4oTestSuiteBuilder(Db4oFixture fixture, Class clazz) {		
		super(clazz);
		if (null == fixture) throw new IllegalArgumentException("fixture");		
		 _fixture = fixture;
	}

	protected Object newInstance(Class clazz) {
		Object instance = super.newInstance(clazz);
		((Db4oTestCase)instance).fixture(_fixture);
		return instance;
	}
}