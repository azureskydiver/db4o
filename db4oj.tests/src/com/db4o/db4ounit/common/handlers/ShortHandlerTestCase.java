/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.internal.handlers.ShortHandler;

import db4ounit.Assert;

public class ShortHandlerTestCase extends TypeHandlerTestCaseBase {
    
    public static void main(String[] args) {
        new ShortHandlerTestCase().runSolo();
    }
    
    private ShortHandler shortHandler() {
        return new ShortHandler(stream());
    }
    
    public void testReadWrite() {
        MockWriteContext writeContext = new MockWriteContext(db());
        Short expected = new Short((short) 0x1020);
        shortHandler().write(writeContext, expected);
        
        MockReadContext readContext = new MockReadContext(writeContext);
        
        Short shortValue = (Short)shortHandler().read(readContext);
        Assert.areEqual(expected, shortValue);
    }
}
