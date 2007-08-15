/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.marshall.*;

import db4ounit.extensions.*;

public class TypeHandlerTestCaseBase extends AbstractDb4oTestCase {
    
    
    
    public static class MockWriteContext implements WriteContext{

        private final ObjectContainer _objectContainer;
        
        private final Buffer _fixedPart;
        
        private final Buffer _payLoad;

        public MockWriteContext(ObjectContainer objectContainer){
            _objectContainer = objectContainer;
            _fixedPart = new Buffer(1000);
            _payLoad = new Buffer(1000);
        }

        public WriteBuffer newBuffer(int length) {
            return new Buffer(length);
        }

        public ObjectContainer objectContainer() {
            return _objectContainer;
        }
        
    }


}