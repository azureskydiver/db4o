/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.internal.handlers.IntHandler;

import db4ounit.Assert;

public class IntHandlerTestCase extends TypeHandlerTestCaseBase {

    public static void main(String[] args) {
        new IntHandlerTestCase().runSolo();
    }
    
    private IntHandler intHandler() {
        return new IntHandler(stream());
    }
    public void testReadWrite() {
        MockWriteContext writeContext = new MockWriteContext(db());
        Integer expected = new Integer(100);
        intHandler().write(writeContext, expected);
        
        MockReadContext readContext = new MockReadContext(writeContext);
        
        Integer intValue = (Integer)intHandler().read(readContext);
        Assert.areEqual(expected, intValue);
    }
}
