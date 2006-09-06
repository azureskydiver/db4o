/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

/**
 * 
 */
package db4ounit.extensions;

import com.db4o.foundation.ArgumentNullException;

import db4ounit.ReflectionTestSuiteBuilder;

public class Db4oTestSuiteBuilder extends ReflectionTestSuiteBuilder {
	
	private Db4oFixture _fixture;
    
	public Db4oTestSuiteBuilder(Db4oFixture fixture, Class clazz) {		
		super(clazz);
        setFixture(fixture);
	}
    
    public Db4oTestSuiteBuilder(Db4oFixture fixture, Class[] classes) {     
        super(classes);
        setFixture(fixture);
    }
    
    private void setFixture(Db4oFixture fixture){
        if (null == fixture) throw new ArgumentNullException("fixture");     
        _fixture = fixture;
    }

	protected Object newInstance(Class clazz) {
		Object instance = super.newInstance(clazz);
		if (instance instanceof Db4oTestCase) {
			((Db4oTestCase)instance).fixture(_fixture);
		}
		return instance;
	}
}