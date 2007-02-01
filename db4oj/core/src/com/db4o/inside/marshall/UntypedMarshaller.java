/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;
import com.db4o.inside.*;

/**
 * @exclude
 */
public abstract class UntypedMarshaller {
    
    MarshallerFamily _family;
    
    public abstract void deleteEmbedded(StatefulBuffer reader);
    
    public abstract Object writeNew(Object obj, boolean restoreLinkOffset, StatefulBuffer writer);

    public abstract Object read(StatefulBuffer reader) throws CorruptionException;
    
    public abstract TypeHandler4 readArrayHandler(Transaction a_trans, Buffer[] a_bytes);

    public abstract boolean useNormalClassRead();
    
    public abstract Object readQuery(Transaction trans, Buffer reader, boolean toArray) throws CorruptionException;

    public abstract QCandidate readSubCandidate(Buffer reader, QCandidates candidates, boolean withIndirection);

	public abstract void defrag(ReaderPair readers);
}
