/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;
import com.db4o.internal.handlers.*;


class ArrayMarshaller1 extends ArrayMarshaller{
    
    protected Buffer prepareIDReader(Transaction trans,Buffer reader) {
        reader._offset = reader.readInt();
        return reader;
    }
    
    public void defragIDs(ArrayHandler arrayHandler,BufferPair readers) {
    	int offset=readers.preparePayloadRead();
        arrayHandler.defrag1(new DefragmentContextImpl(_family, readers, true));
        readers.seek(offset);
    }
}
