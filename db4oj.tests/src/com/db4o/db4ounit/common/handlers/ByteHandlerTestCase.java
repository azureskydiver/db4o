/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.internal.handlers.ByteHandler;

import db4ounit.Assert;

public class ByteHandlerTestCase extends TypeHandlerTestCaseBase {

    public static void main(String[] args) {
        new ByteHandlerTestCase().runSolo();
    }
    
    private ByteHandler byteHandler() {
        return new ByteHandler(stream());
    }
    public void testReadWrite() {
        MockWriteContext writeContext = new MockWriteContext(db());
        Byte expected = new Byte((byte)0x61);
        byteHandler().write(writeContext, expected);
        
        MockReadContext readContext = new MockReadContext(writeContext);
        
        Byte byteValue = (Byte)byteHandler().read(readContext);
        Assert.areEqual(expected, byteValue);
    }
}
