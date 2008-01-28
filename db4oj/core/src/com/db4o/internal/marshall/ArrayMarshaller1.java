/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;


class ArrayMarshaller1 extends ArrayMarshaller{
    
    protected ByteArrayBuffer prepareIDReader(Transaction trans,ByteArrayBuffer reader) {
        reader._offset = reader.readInt();
        return reader;
    }
    
    
}
