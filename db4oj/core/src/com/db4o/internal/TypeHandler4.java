/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public interface TypeHandler4 extends Comparable4 {
	
	void cascadeActivation(Transaction trans, Object obj, int depth, boolean activate);
	
	void deleteEmbedded(MarshallerFamily mf, StatefulBuffer buffer) throws Db4oIOException;
	
    int linkLength();
   
	Object read(MarshallerFamily mf, StatefulBuffer buffer, boolean redirect) throws CorruptionException, Db4oIOException;
    
	Object readQuery(Transaction trans, MarshallerFamily mf, boolean withRedirection, Buffer buffer, boolean toArray) throws CorruptionException, Db4oIOException;
	
    QCandidate readSubCandidate(MarshallerFamily mf, Buffer buffer, QCandidates candidates, boolean withIndirection);

	void defrag(MarshallerFamily mf, BufferPair readers, boolean redirect);

	Object read(ReadContext context);
	
    void write(WriteContext context, Object obj);
	
}
