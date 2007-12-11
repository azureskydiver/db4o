/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.ext.*;
import com.db4o.internal.*;


class ArrayMarshaller0  extends ArrayMarshaller{
    
    protected BufferImpl prepareIDReader(Transaction trans,BufferImpl reader) throws Db4oIOException {
    	return reader.readEmbeddedObject(trans);
    }
    
}
