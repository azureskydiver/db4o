/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta;


public abstract class ItemTestCaseBase extends TransparentActivationTestCaseBase {
    
	private Class _clazz;
	
    protected void store() throws Exception {
        Object value = createItem();
        _clazz = value.getClass();
        store(value);
    }
    
    public void test() throws Exception {
        Object item = retrieveOnlyInstance(_clazz);
        assertRetrievedItem(item);
        assertItemValue(item);
    }
    
    protected abstract void assertItemValue(Object obj) throws Exception;
     
    protected abstract Object createItem() throws Exception;
    
    protected abstract void assertRetrievedItem(Object obj) throws Exception;
}
