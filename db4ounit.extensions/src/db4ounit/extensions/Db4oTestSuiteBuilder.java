/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

/**
 * 
 */
package db4ounit.extensions;

import java.lang.reflect.Method;

import com.db4o.foundation.ArgumentNullException;

import db4ounit.*;

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

    protected boolean isApplicable(Class clazz) {
    	return _fixture.accept(clazz);
    }
    
	protected Object newInstance(Class clazz) {
		Object instance = super.newInstance(clazz);
		if (instance instanceof AbstractDb4oTestCase) {
			((AbstractDb4oTestCase)instance).fixture(_fixture);
		}
		return instance;
	}
	
	protected Test createTest(Object instance, Method method) {
		if (instance instanceof AbstractDb4oTestCase) {
			return new TestMethod(instance, method, Db4oFixtureLabelProvider.DEFAULT); 
		}
		return super.createTest(instance, method);
	}
}