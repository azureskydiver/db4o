/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.internal.handlers.CharHandler;

import db4ounit.Assert;

public class CharHandlerTestCase extends TypeHandlerTestCaseBase {
    
    public static void main(String[] args) {
        new CharHandlerTestCase().runSolo();
    }
    
    private CharHandler charHandler() {
        return new CharHandler(stream());
    }
    
    public void testReadWrite() {
        MockWriteContext writeContext = new MockWriteContext(db());
        Character expected = new Character((char)0x4e2d);
        charHandler().write(writeContext, expected);
        
        MockReadContext readContext = new MockReadContext(writeContext);
        Character charValue = (Character)charHandler().read(readContext);
        
        Assert.areEqual(expected, charValue);
    }
}
