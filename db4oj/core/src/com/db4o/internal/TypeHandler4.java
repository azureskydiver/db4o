/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public interface TypeHandler4 extends Comparable4 {
	
	void deleteEmbedded(MarshallerFamily mf, StatefulBuffer buffer) throws Db4oIOException;
	
	void defrag(MarshallerFamily mf, BufferPair readers, boolean redirect);

	Object read(ReadContext context);
	
    void write(WriteContext context, Object obj);
	
}
