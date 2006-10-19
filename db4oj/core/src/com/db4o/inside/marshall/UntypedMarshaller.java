/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;

/**
 * @exclude
 */
public abstract class UntypedMarshaller {
    
    MarshallerFamily _family;
    
    public abstract void deleteEmbedded(YapWriter reader);
    
    public abstract Object writeNew(Object obj, boolean restoreLinkOffset, YapWriter writer);

    public abstract Object read(YapWriter reader) throws CorruptionException;
    
    public abstract TypeHandler4 readArrayHandler(Transaction a_trans, YapReader[] a_bytes);

    public abstract boolean useNormalClassRead();
    
    public abstract Object readQuery(Transaction trans, YapReader reader, boolean toArray) throws CorruptionException;

    public abstract QCandidate readSubCandidate(YapReader reader, QCandidates candidates, boolean withIndirection);

	public abstract void defrag(ReaderPair readers);
}
