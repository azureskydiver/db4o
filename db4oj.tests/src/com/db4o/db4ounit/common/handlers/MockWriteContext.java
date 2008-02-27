/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.marshall.*;


public class MockWriteContext extends MockMarshallingContext implements WriteContext{

    public MockWriteContext(ObjectContainer objectContainer) {
        super(objectContainer);
    }
    
    public void writeObject(TypeHandler4 handler, Object obj) {
        handler.write(this, obj);
    }

    public void writeAny(Object obj) {
        ClassMetadata classMetadata = container().classMetadataForObject(obj);
        writeInt(classMetadata.getID());
        classMetadata.write(this, obj);
    }

    public ReservedBuffer reserve(int length) {
        ReservedBuffer reservedBuffer = new ReservedBuffer() {
            final int reservedBufferOffset = offset();
            public void writeBytes(byte[] bytes) {
                int currentOffset = offset();
                seek(reservedBufferOffset);
                MockWriteContext.this.writeBytes(bytes);
                seek(currentOffset);
            }
        };
        return reservedBuffer;
    }
    
}
