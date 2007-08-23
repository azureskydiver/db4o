/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.handlers;

import com.db4o.internal.handlers.*;
import db4ounit.Assert;

public class BooleanHandlerTestCase extends TypeHandlerTestCaseBase {
	
    public static void main(String[] arguments) {
        new BooleanHandlerTestCase().runSolo();
    }
    
    private BooleanHandler booleanHandler() {
        return new BooleanHandler(stream());
    }

	public void testReadWriteTrue(){
	    MockWriteContext writeContext = new MockWriteContext(db());
	    booleanHandler().write(writeContext, Boolean.TRUE);
	    
	    MockReadContext readContext = new MockReadContext(writeContext);
	    Boolean res = (Boolean)booleanHandler().read(readContext);
	    
	    Assert.areEqual(Boolean.TRUE, res);
	}
	
	public void testReadWriteFalse(){
	    MockWriteContext writeContext = new MockWriteContext(db());
	    booleanHandler().write(writeContext, Boolean.FALSE);
	    
	    MockReadContext readContext = new MockReadContext(writeContext);
	    Boolean res = (Boolean)booleanHandler().read(readContext);
	    
	    Assert.areEqual(Boolean.FALSE, res);
	}


}
