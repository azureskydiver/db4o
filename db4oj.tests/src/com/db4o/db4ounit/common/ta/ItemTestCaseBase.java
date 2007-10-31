/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta;

import com.db4o.ext.*;

import db4ounit.extensions.fixtures.*;


public abstract class ItemTestCaseBase
	extends TransparentActivationTestCaseBase
	implements OptOutDefragSolo {
    
	private Class _clazz;
	protected long id;
	protected Db4oUUID uuid;
	
    protected void store() throws Exception {
        Object value = createItem();
        _clazz = value.getClass();
        store(value);
        id = db().ext().getID(value);
        uuid = db().ext().getObjectInfo(value).getUUID();
    }
    
    public void testQuery() throws Exception {
        Object item = retrieveOnlyInstance(_clazz);
        assertRetrievedItem(item);
        assertItemValue(item);
    }
    
    public void testDeactivate() throws Exception {	
    	Object item = retrieveOnlyInstance(_clazz);
    	db().deactivate(item, 1);
    	assertNullItem(item);    	
	}
    
    protected abstract void assertNullItem(Object obj) throws Exception;
    
    protected abstract void assertItemValue(Object obj) throws Exception;
     
    protected abstract Object createItem() throws Exception;
    
    protected abstract void assertRetrievedItem(Object obj) throws Exception;
}
