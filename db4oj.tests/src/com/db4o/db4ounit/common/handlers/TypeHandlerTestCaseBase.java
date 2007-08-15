/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.marshall.*;

import db4ounit.extensions.*;

public class TypeHandlerTestCaseBase extends AbstractDb4oTestCase {
    
    
    
    public static class MockMarshallingContext implements ReadContext, WriteContext{

        private final ObjectContainer _objectContainer;
        
        private final Buffer _header;
        
        private final Buffer _payLoad;
        
        public MockMarshallingContext(ObjectContainer objectContainer){
            _objectContainer = objectContainer;
            _header = new Buffer(1000);
            _payLoad = new Buffer(1000);
        }

        public WriteBuffer newBuffer(int length) {
            return new Buffer(length);
        }

        public ObjectContainer objectContainer() {
            return _objectContainer;
        }

		public void useVariableLength() {
			// _header.writeInt(_pa)
			// TODO Auto-generated method stub
		}

		public byte readByte() {
			// TODO Auto-generated method stub
			return 0;
		}

		public int readInt() {
			// TODO Auto-generated method stub
			return 0;
		}

		public void writeByte(byte b) {
			// TODO Auto-generated method stub
			
		}

		public void writeInt(int i) {
			// TODO Auto-generated method stub
			
		}
        
    }


}