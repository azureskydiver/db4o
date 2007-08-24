/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.handlers;

import com.db4o.internal.handlers.*;
import db4ounit.Assert;

public class BooleanHandlerTestCase extends TypeHandlerTestCaseBase {
	
    public static void main(String[] arguments) {
        new BooleanHandlerTestCase().runSolo();
    }
    
    public static class Item {
    	public Boolean _boolWrapper;
    	public boolean _bool;
    	
    	public Item(Boolean boolWrapper, boolean bool){
    		_boolWrapper = boolWrapper;
    		_bool = bool;
    	}
    }
    
    private BooleanHandler booleanHandler() {
        return new BooleanHandler(stream());
    }

	public void testReadWriteTrue(){
		doTestReadWrite(Boolean.TRUE);
	}
	
	public void testReadWriteFalse(){
		doTestReadWrite(Boolean.FALSE);
	}
	
	public void doTestReadWrite(Boolean b){
	    MockWriteContext writeContext = new MockWriteContext(db());
	    booleanHandler().write(writeContext, b);
	    
	    MockReadContext readContext = new MockReadContext(writeContext);
	    Boolean res = (Boolean)booleanHandler().read(readContext);
	    
	    Assert.areEqual(b, res);
	}
	
    public void testStoreObject() throws Exception{
        Item storedItem = new Item(Boolean.FALSE, true);
        db().set(storedItem);
        db().purge(storedItem);
    
        Item readItem = (Item) retrieveOnlyInstance(Item.class);
        
        Assert.areNotSame(storedItem, readItem);
        Assert.areEqual(storedItem._bool, readItem._bool);
        Assert.areEqual(storedItem._boolWrapper, readItem._boolWrapper);
    }


}
