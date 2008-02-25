/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

/**
 * 
 */
package db4ounit.extensions;

import java.lang.reflect.Method;

import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.fixtures.*;

public class Db4oTestSuiteBuilder extends ReflectionTestSuiteBuilder {
		
	private Db4oFixture _fixture;
    
	public Db4oTestSuiteBuilder(Db4oFixture fixture, Class clazz) {		
		this(fixture, new Class[] { clazz });
	}
    
    public Db4oTestSuiteBuilder(Db4oFixture fixture, Class[] classes) {     
        super(classes);
        fixture(fixture);
    }
    
    private void fixture(Db4oFixture fixture){
        if (null == fixture) throw new ArgumentNullException("fixture");     
        _fixture = fixture;
    }

    protected boolean isApplicable(Class clazz) {
    	return _fixture.accept(clazz);
    }
    
    protected Iterator4 fromClass(final Class clazz) {
    	return (Iterator4) AbstractDb4oTestCase.FIXTURE_VARIABLE.with(_fixture, new SafeClosure4() {
    		public Object run() {
    			return baseFromClass(clazz);
    		}
    	});
    }
    
    protected Test fromMethod(Class clazz, Method method) {
    	final Test test = super.fromMethod(clazz, method);
		if (AbstractDb4oTestCase.class.isAssignableFrom(clazz)) {
			return new FixtureDecoration(test, null, AbstractDb4oTestCase.FIXTURE_VARIABLE, _fixture); 
		}
		return test;
	}

	private Iterator4 baseFromClass(final Class clazz) {
		return super.fromClass(clazz);
	}
}