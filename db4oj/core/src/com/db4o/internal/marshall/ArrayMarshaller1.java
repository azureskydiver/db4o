/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;
import com.db4o.internal.handlers.*;


class ArrayMarshaller1 extends ArrayMarshaller{
    
    protected BufferImpl prepareIDReader(Transaction trans,BufferImpl reader) {
        reader._offset = reader.readInt();
        return reader;
    }
    
    public void defragIDs(ArrayHandler arrayHandler,DefragmentContext context) {
    	int offset= preparePayloadRead(context);
        arrayHandler.defrag1(new DefragmentContextImpl(context, true));
        context.seek(offset);
    }
    
    private int preparePayloadRead(DefragmentContext context) {
        int newPayLoadOffset = context.readInt();
        context.readInt();  // skip length, not needed
        int linkOffSet = context.offset();
        context.seek(newPayLoadOffset);
        return linkOffSet;
    }

    
    
}
