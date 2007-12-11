/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.*;


public abstract class StringMarshaller {
    
    public abstract boolean inlinedStrings();
    
    public abstract BufferImpl readIndexEntry(StatefulBuffer parentSlot) throws CorruptionException, IllegalArgumentException, Db4oIOException;

	public abstract void defrag(Buffer reader);

}
