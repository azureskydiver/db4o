/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions;

import java.lang.reflect.Field;

import com.db4o.*;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.*;
import com.db4o.reflect.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.Db4oSolo;

public class Db4oTestCase implements TestCase, TestLifeCycle {
    
	private Db4oFixture _fixture;
	
	public void fixture(Db4oFixture fixture) {
		_fixture = fixture;
	}

	public Db4oFixture fixture() {
		return _fixture;
	}
    
    protected void reopen() throws Exception{
        fixture().close();
        fixture().open();
    }
	
	public void setUp() throws Exception {
        _fixture.clean();
		configure();
		_fixture.open();
		store();
        _fixture.close();
        _fixture.open();
	}
	
	public void tearDown() throws Exception {
		_fixture.close();
        _fixture.clean();
	}

	protected void configure() {}
	
	protected void store() throws Exception {}

	protected ExtObjectContainer db() {
		return fixture().db();
	}
	
	protected Class[] testCases() {
		return new Class[] { getClass() };
	}
	
	public int runSolo() {
		return new TestRunner(
					new Db4oTestSuiteBuilder(
							new Db4oSolo(), testCases())).run();
	}

	protected YapStream stream() {
	    return (YapStream) db();
	}

	protected Transaction trans() {
	    return stream().getTransaction();
	}

	protected Transaction systemTrans() {
	    return stream().getSystemTransaction();
	}
    
    protected Query newQuery(){
        return db().query();
    }
    
    protected Reflector reflector(){
        return stream().reflector();
    }

	protected void indexField(Class clazz, String fieldName) {
		Db4o.configure().objectClass(clazz).objectField(fieldName).indexed(true);
	}

	protected void indexAllFields(Class clazz) {
		final Field[] fields = clazz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			indexField(clazz, fields[i].getName());
		}
		final Class superclass = clazz.getSuperclass();
		if (superclass != null) {
			indexAllFields(superclass);
		}
	}
}
