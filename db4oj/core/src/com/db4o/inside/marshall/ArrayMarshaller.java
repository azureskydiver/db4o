/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;

/**
 * @exclude
 */
public abstract class ArrayMarshaller {
    
    public MarshallerFamily _family;
    
    public abstract void deleteEmbedded(YapArray arrayHandler, YapWriter reader);
    
    public final TreeInt collectIDs(YapArray arrayHandler, TreeInt tree, YapWriter reader){
        Transaction trans = reader.getTransaction();
        return arrayHandler.collectIDs1(trans, tree, prepareIDReader(trans,reader));
    }

    public abstract void defragIDs(YapArray arrayHandler,ReaderPair readers);
    
    public abstract void calculateLengths(Transaction trans, ObjectHeaderAttributes header, YapArray handler, Object obj, boolean topLevel);
    
    public abstract Object read(YapArray arrayHandler,  YapWriter reader) throws CorruptionException;
    
    public abstract void readCandidates(YapArray arrayHandler, YapReader reader, QCandidates candidates);
    
    public abstract Object readQuery(YapArray arrayHandler, Transaction trans, YapReader reader) throws CorruptionException;
    
    public abstract Object writeNew(YapArray arrayHandler, Object obj, boolean topLevel, YapWriter writer);

    protected abstract YapReader prepareIDReader(Transaction trans,YapReader reader);
}
